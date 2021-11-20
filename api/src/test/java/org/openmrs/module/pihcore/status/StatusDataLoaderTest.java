package org.openmrs.module.pihcore.status;

import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.pihcore.PihCoreContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class StatusDataLoaderTest extends PihCoreContextSensitiveTest {

    @Autowired
    StatusDataEvaluator evaluator;

    @Autowired
    PatientService patientService;

    @Autowired
    MessageSourceService messageSourceService;

    public List<File> setup() {
        List<File> filesAdded = new ArrayList<>();
        filesAdded.add(addResourceToConfigurationDirectory("pih/statusData", "birthdate.sql"));
        filesAdded.add(addResourceToConfigurationDirectory("pih/statusData", "testStatuses.yml"));
        return filesAdded;
    }

    public void teardown(List<File> filesAdded) {
        cleanUpConfigurationDirectory(filesAdded);
    }

    @Test
    public void testGetStatusDataDefinitions() {
        List<File> filesAdded = setup();
        List<StatusDataDefinition> statusDataList = StatusDataLoader.getStatusDataDefinitions("testStatuses.yml");
        assertThat(statusDataList.size(), is(2));
        {
            StatusDataDefinition d = statusDataList.get(0);
            assertThat(d.getId(), is("gender"));
            assertThat(d.getLabelCode(), is("label.gender"));
            assertThat(d.getStatusDataQuery(), is("select gender from person where person_id = @patientId"));
            assertThat(d.getValueExpression(), is("#if($data.GENDER == 'F')$fn.translate('gender.female')#{elseif}($data.GENDER == 'M')$fn.translate('gender.male')#{else}#end"));
            assertThat(d.getFormatExpression(), is("#if($data.GENDER == 'F')femaleFormat#{elseif}($data.GENDER == 'M')maleFormat#{else}#end"));
        }
        {
            StatusDataDefinition d = statusDataList.get(1);
            assertThat(d.getId(), is("age"));
            assertThat(d.getLabelCode(), is("label.age"));
            assertThat(d.getStatusDataQuery(), is("birthdate.sql"));
            assertThat(d.getValueExpression(), is("$fn.yearsSince($data.BIRTHDATE)"));
            assertNull(d.getFormatExpression());
        }
        teardown(filesAdded);
    }

    @Test
    public void testGetStatusDataConfigurationPaths() {
        List<File> filesAdded = setup();
        filesAdded.add(addResourceToConfigurationDirectory("pih/statusData/nested", "nestedStatuses.yml"));
        List<String> paths = StatusDataLoader.getStatusDataConfigurationPaths();
        assertThat(paths.size(), is(2));
        assertThat(paths, containsInAnyOrder("testStatuses.yml", "nested/nestedStatuses.yml"));
        teardown(filesAdded);
    }
}
