package org.openmrs.module.pihcore.htmlformentry.velocity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.VelocityContext;
import org.openmrs.Concept;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.FormEntrySession;
import org.openmrs.module.htmlformentry.velocity.VelocityContextContentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class PihVelocityContextContentProvider implements VelocityContextContentProvider {

    @Autowired
    private ConditionListVelocityContextContentProvider conditionListVelocityContextContentProvider;

    @Override
    public void populateContext(FormEntrySession formEntrySession, VelocityContext velocityContext) {
        Patient patient = formEntrySession.getPatient();

        List<Concept> mentalHealthDiagnosesConcepts = getMentalHealthDiagnoses();

        List<Concept> ncdDiagnosesConcepts = getNcdDiagnoses();

        List<Concept> conditionListConcepts = conditionListVelocityContextContentProvider.getActiveConditionsConcepts(patient);

        velocityContext.put("mentalHealthDiagnosesConcepts", mentalHealthDiagnosesConcepts);
        velocityContext.put("ncdDiagnosesConcepts", ncdDiagnosesConcepts);
        velocityContext.put("patientHasMentalHealthCondition",
                CollectionUtils.containsAny(mentalHealthDiagnosesConcepts, conditionListConcepts));
        velocityContext.put("patientHasNcdCondition", CollectionUtils.containsAny(ncdDiagnosesConcepts, conditionListConcepts));
    }

    private List<Concept> getNcdDiagnoses() {
        List<Concept> concepts = new ArrayList<Concept>();

        addProgramConceptToList(concepts, "DIABETES", "PIH");
        addProgramConceptToList(concepts, "HYPERTENSION", "PIH");
        addProgramConceptToList(concepts, "RENAL FAILURE", "PIH");
        addProgramConceptToList(concepts, "HEART FAILURE", "PIH");
        addProgramConceptToList(concepts, "LIVER FAILURE", "PIH");
        addProgramConceptToList(concepts, "ASTHMA", "PIH");
        addProgramConceptToList(concepts, "CHRONIC OBSTRUCTIVE PULMONARY DISEASE", "PIH");
        addProgramConceptToList(concepts, "Sickle-Cell Anemia", "PIH");
        addProgramConceptToList(concepts, "SPLENOMEGALY", "PIH");

        return concepts;
    }

    private List<Concept> getMentalHealthDiagnoses() {
        addProgramConceptToList(concepts, "PSYCHOSIS", "PIH");
        addProgramConceptToList(concepts, "Bipolar disorder", "PIH");
        addProgramConceptToList(concepts, "115924", "CIEL"); // Bipolar mania
        addProgramConceptToList(concepts, "119541", "CIEL"); // Bipolar depression
        addProgramConceptToList(concepts, "SCHIZOPHRENIA", "PIH");
        addProgramConceptToList(concepts, "Psychosomatic problems", "PIH");
        addProgramConceptToList(concepts, "Hyperkinetic Behavior", "PIH");
        addProgramConceptToList(concepts, "Conduct disorder", "PIH");
        addProgramConceptToList(concepts, "Mental handicap", "PIH");
        addProgramConceptToList(concepts, "DEMENTIA", "PIH");
        addProgramConceptToList(concepts, "EPILEPSY", "PIH");
        addProgramConceptToList(concepts, "ANXIETY DISORDER", "PIH");
        addProgramConceptToList(concepts, "Post traumatic stress disorder", "PIH");
        addProgramConceptToList(concepts, "DEPRESSION", "PIH");
        addProgramConceptToList(concepts, "Manic episode", "PIH");
        addProgramConceptToList(concepts, "Mood disorder", "PIH");
        addProgramConceptToList(concepts, "121725", "CIEL"); // Alcohol abuse
        addProgramConceptToList(concepts, "112603", "CIEL"); // Drug abuse
        addProgramConceptToList(concepts, "137668", "CIEL"); // behaviornal disorder
        addProgramConceptToList(concepts, "14629", "PIH");   // emotional disorder
        addProgramConceptToList(concepts, "121303", "CIEL"); // autism
        addProgramConceptToList(concepts, "156923", "CIEL"); // intellectual
        addProgramConceptToList(concepts, "160197", "CIEL"); // psychosomatic
        addProgramConceptToList(concepts, "139545", "CIEL"); // GAD
        addProgramConceptToList(concepts, "130966", "CIEL"); // Panic disorder
        addProgramConceptToList(concepts, "113517", "CIEL"); // Psychosis

        return concepts;
    }

    private void addProgramConceptToList(List<Concept> concepts, String code, String sourceName) {
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByMapping(code, sourceName);
        if (concept != null) {
            concepts.add(concept);
        }
    }
}
