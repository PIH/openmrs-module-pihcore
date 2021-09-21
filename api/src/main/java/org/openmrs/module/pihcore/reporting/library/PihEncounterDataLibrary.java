package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.disposition.DispositionService;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.CountConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.ObsValueTextAsCodedConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.encounter.definition.AgeAtEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.AuditInfoEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ConvertedEncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterProviderDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.ObsForEncounterDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PihEncounterDataLibrary extends BaseDefinitionLibrary<EncounterDataDefinition> {

    @Autowired
    ConceptService conceptService;

    @Autowired
    EncounterService encounterService;

    @Autowired
    DispositionService dispositionService;

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    DataConverterLibrary converters;

    @Override
    public Class<? super EncounterDataDefinition> getDefinitionType() {
        return EncounterDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "pihcore.encounterData.";
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAuditInfo() {
        return new AuditInfoEncounterDataDefinition();
    }

    @DocumentedDefinition
    public EncounterDataDefinition getCreatorName() {
        return convert(getAuditInfo(), converters.getAuditInfoCreatorNameConverter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getDateCreated() {
        return convert(getAuditInfo(), converters.getAuditInfoDateCreatedConverter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getEncounterProvider() {
        EncounterProviderDataDefinition epdd = new EncounterProviderDataDefinition();
        epdd.setSingleProvider(true);
        return new ConvertedEncounterDataDefinition(epdd, new DataConverter[]{new ObjectFormatter()});
    }

    @DocumentedDefinition
    public EncounterDataDefinition getPatientAgeAtEncounter() {
        return new AgeAtEncounterDataDefinition();
    }

    @DocumentedDefinition
    public EncounterDataDefinition getSingleObsInEncounter(Concept concept) {
        ObsForEncounterDataDefinition d = new ObsForEncounterDataDefinition();
        d.setQuestion(concept);
        d.setSingleObs(true);
        return d;
    }

    @DocumentedDefinition
    public EncounterDataDefinition getDisposition() {
        return getSingleObsInEncounter(dispositionService.getDispositionDescriptor().getDispositionConcept());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAllProviders() {
        EncounterProviderDataDefinition dd = new EncounterProviderDataDefinition();
        dd.setSingleProvider(false);
        ChainedConverter itemConverter = new ChainedConverter(new PropertyConverter(Provider.class, "person"), new PropertyConverter(Person.class, "personName"),
                new ObjectFormatter("{givenName} {familyName}"));
        return new ConvertedEncounterDataDefinition(dd, new CollectionConverter(itemConverter, false, null), new ObjectFormatter(", "));
    }

    @DocumentedDefinition
    public EncounterDataDefinition getNumberOfProviders() {
        EncounterProviderDataDefinition dd = new EncounterProviderDataDefinition();
        dd.setSingleProvider(false);
        return new ConvertedEncounterDataDefinition(dd, new CountConverter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getClerk() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_ADMINISTRATIVECLERK_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getNurse() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_NURSE_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getConsultingClinician() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_CONSULTINGCLINICIAN_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getDispenser() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_DISPENSER_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getRadiologyTechnician() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_RADIOLOGYTECHNICIAN_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getOrderingProvider() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_ORDERINGPROVIDER_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getPrincipalResultsInterpreter() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_PRINCIPALRESULTSINTERPRETER_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAnesthesiologist() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_ANESTHESIOLOGIST_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAttendingSurgeonName() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_ATTENDINGSURGEON_UUID);
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAssistingSurgeon() {
        return getProvider(PihEmrConfigConstants.ENCOUNTERROLE_ASSISTINGSURGEON_UUID);
    }

    private EncounterDataDefinition getProvider(String encounterRoleUuid) {
        EncounterProviderDataDefinition dd = new EncounterProviderDataDefinition();
        dd.setEncounterRole(encounterService.getEncounterRoleByUuid(encounterRoleUuid));
        return new ConvertedEncounterDataDefinition(dd,
                new PropertyConverter(Provider.class, "person"),
                new PropertyConverter(Person.class, "personName"),
                new ObjectFormatter("{givenName} {familyName}")
        );
    }

    @DocumentedDefinition
    public EncounterDataDefinition getAdmissionStatus() {
        ObsForEncounterDataDefinition dd = new ObsForEncounterDataDefinition();
        // concept with synonyms "Type of Patient" and "Admission Status", possible answers are Ambulatory and Hospitalized
        dd.setQuestion(conceptService.getConceptByMapping("3289", "PIH"));
        return new ConvertedEncounterDataDefinition(dd, new PropertyConverter(Obs.class, "valueCoded"), new ObjectFormatter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getRequestedAdmissionLocationName() {
        ObsForEncounterDataDefinition dd = new ObsForEncounterDataDefinition();
        dd.setQuestion(dispositionService.getDispositionDescriptor().getAdmissionLocationConcept());
        return new ConvertedEncounterDataDefinition(dd, new ObsValueTextAsCodedConverter<Location>(Location.class), new ObjectFormatter());
    }

    @DocumentedDefinition
    public EncounterDataDefinition getRequestedTransferLocationName() {
        ObsForEncounterDataDefinition dd = new ObsForEncounterDataDefinition();
        dd.setQuestion(dispositionService.getDispositionDescriptor().getInternalTransferLocationConcept());
        return new ConvertedEncounterDataDefinition(dd, new ObsValueTextAsCodedConverter<Location>(Location.class), new ObjectFormatter());
    }

    protected ConvertedEncounterDataDefinition convert(EncounterDataDefinition d, DataConverter... converters) {
        return new ConvertedEncounterDataDefinition(d, converters);
    }
}
