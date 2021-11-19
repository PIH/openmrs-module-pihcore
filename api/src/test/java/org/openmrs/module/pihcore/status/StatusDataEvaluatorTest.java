package org.openmrs.module.pihcore.status;

import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StatusDataEvaluatorTest extends PihCoreContextSensitiveTest {

    @Autowired
    StatusDataEvaluator evaluator;

    @Autowired
    PatientService patientService;

    @Autowired
    MessageSourceService messageSourceService;

    @Test
    public void testEvaluateDefinition() {
        messageSourceService.addPresentation(new PresentationMessage("patient.mostRecentWeightDate", Context.getLocale(), "Date of most recent weight", ""));
        Patient patient = patientService.getPatient(7);
        StatusDataDefinition definition = new StatusDataDefinition();
        definition.setLabelCode("patient.mostRecentWeightDate");
        definition.setStatusDataQuery("select o.value_numeric as WEIGHT, o.obs_datetime as WEIGHTDATE from obs o where o.person_id = @patientId order by o.obs_datetime desc limit 1");
        definition.setValueExpression("$data.WEIGHT on $fn.formatDate($data.WEIGHTDATE, 'yyyy-MM-dd')");
        definition.setFormatExpression("normalValue");

        StatusData statusData = evaluator.evaluate(patient, definition);
        assertThat(statusData.getLabel(), is("Date of most recent weight"));
        assertThat(statusData.getDisplayValue(), is("61.0 on 2008-08-19"));
        assertThat(statusData.getDisplayFormat(), is("normalValue"));

        definition.setFormatExpression("#if($data.WEIGHT > 100)high#{elseif}($data.WEIGHT > 50)medium#{else}low#end");

        statusData = evaluator.evaluate(patient, definition);
        assertThat(statusData.getLabel(), is("Date of most recent weight"));
        assertThat(statusData.getDisplayValue(), is("61.0 on 2008-08-19"));
        assertThat(statusData.getDisplayFormat(), is("medium"));
    }

    @Test
    public void testEvaluateFromFile() {
        List<File> filesAdded = new ArrayList<>();
        filesAdded.add(addResourceToConfigurationDirectory("pih/statusData", "birthdate.sql"));
        filesAdded.add(addResourceToConfigurationDirectory("pih/statusData", "testStatuses.yml"));
        messageSourceService.addPresentation(new PresentationMessage("label.gender", Context.getLocale(), "Gender", ""));
        messageSourceService.addPresentation(new PresentationMessage("label.age", Context.getLocale(), "Age", ""));
        messageSourceService.addPresentation(new PresentationMessage("gender.female", Context.getLocale(), "Female", ""));
        messageSourceService.addPresentation(new PresentationMessage("gender.male", Context.getLocale(), "Male", ""));

        Patient patient = patientService.getPatient(7);
        List<StatusData> statusDataList = evaluator.evaluate(patient, "testStatuses.yml");
        assertThat(statusDataList.size(), is(2));
        {
            StatusData d = statusDataList.get(0);
            assertThat(d.getDefinitionId(), is("gender"));
            assertThat(d.getLabel(), is("Gender"));
            assertThat(d.getDisplayValue(), is("Female"));
            assertThat(d.getDisplayFormat(), is("femaleFormat"));
        }
        {
            StatusData d = statusDataList.get(1);
            assertThat(d.getDefinitionId(), is("age"));
            assertThat(d.getLabel(), is("Age"));
            assertThat(d.getDisplayValue(), is(patient.getAge().toString()));
            assertThat(d.getDisplayFormat(), is(""));
        }

        cleanUpConfigurationDirectory(filesAdded);
    }
}
