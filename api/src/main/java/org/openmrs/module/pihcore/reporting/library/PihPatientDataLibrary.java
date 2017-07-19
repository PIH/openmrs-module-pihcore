package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.User;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.EncounterTypes;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.CountConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.EarliestCreatedConverter;
import org.openmrs.module.reporting.data.converter.MostRecentlyCreatedConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PersonToPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PreferredIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeAtDateOfOtherDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PihPatientDataLibrary extends BaseDefinitionLibrary<PatientDataDefinition> {

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    DataConverterLibrary converters;

    @Override
    public Class<? super PatientDataDefinition> getDefinitionType() {
        return PatientDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "mirebalais.patientDataCalculation.";
    }

    // Patient Identifier

    @DocumentedDefinition
    public PatientDataDefinition getPreferredPrimaryIdentifierObject() {
        PreferredIdentifierDataDefinition d = new PreferredIdentifierDataDefinition();
        d.setIdentifierType(emrApiProperties.getPrimaryIdentifierType());
        return d;
    }

    @DocumentedDefinition
    public PatientDataDefinition getPreferredPrimaryIdentifier() {
        return convert(getPreferredPrimaryIdentifierObject(), converters.getIdentifierConverter());
    }

    @DocumentedDefinition
    public PatientDataDefinition getPreferredPrimaryIdentifierLocation() {
        return convert(getPreferredPrimaryIdentifierObject(), converters.getIdentifierLocationNameConverter());
    }

    @DocumentedDefinition
    public PatientDataDefinition getAllPrimaryIdentifiers() {
        PatientIdentifierDataDefinition d = new PatientIdentifierDataDefinition();
        d.setTypes(Arrays.asList(emrApiProperties.getPrimaryIdentifierType()));
        return d;
    }

    @DocumentedDefinition
    public PatientDataDefinition getNumberOfPrimaryIdentifiers() {
        return convert(getAllPrimaryIdentifiers(), converters.getCollectionSizeConverter());
    }


    // TODO: See if we need the below identifier libraries to be specific like they are or if we can generalize like the above

    @DocumentedDefinition("numberOfZlEmrIds")
    public PatientDataDefinition getNumberOfZlEmrIds() {
        return getIdentifiersOf(Metadata.lookup(PihHaitiPatientIdentifierTypes.ZL_EMR_ID), new CountConverter());
    }

    @DocumentedDefinition("numberOfDossierNumbers")
    public PatientDataDefinition getNumberOfDossierNumbers() {
        return getIdentifiersOf(Metadata.lookup(PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER), new CountConverter());
    }

    @DocumentedDefinition("numberOfHivEmrIds")
    public PatientDataDefinition getNumberOfHivEmrIds() {
        return getIdentifiersOf(Metadata.lookup(PihHaitiPatientIdentifierTypes.HIVEMR_V1), new CountConverter());
    }

    @DocumentedDefinition("preferredZlEmrId.identifier")
    public PatientDataDefinition getPreferredZlEmrIdIdentifier() {
        return getPreferredIdentifierOf(
                Metadata.lookup(PihHaitiPatientIdentifierTypes.ZL_EMR_ID),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("mostRecentZlEmrId.location")
    public PatientDataDefinition getMostRecentZlEmrIdLocation() {
        return getMostRecentIdentifierOf(
                Metadata.lookup(PihHaitiPatientIdentifierTypes.ZL_EMR_ID),
                new PropertyConverter(PatientIdentifier.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("mostRecentDossierNumber.identifier")
    public PatientDataDefinition getMostRecentDossierNumberIdentifier() {
        return getMostRecentIdentifierOf(
                Metadata.lookup(PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    @DocumentedDefinition("mostRecentHivEmrId.identifier")
    public PatientDataDefinition getMostRecentHivEmrIdIdentifier() {
        return getMostRecentIdentifierOf(
                Metadata.lookup(PihHaitiPatientIdentifierTypes.HIVEMR_V1),
                new PropertyConverter(PatientIdentifier.class, "identifier"));
    }

    private PatientDataDefinition getIdentifiersOf(PatientIdentifierType patientIdentifierType, DataConverter... converters) {
        return new ConvertedPatientDataDefinition(
                new PatientIdentifierDataDefinition(null, patientIdentifierType),
                converters);
    }

    private PatientDataDefinition getPreferredIdentifierOf(PatientIdentifierType patientIdentifierType, DataConverter... converters) {
        PatientIdentifierDataDefinition dd = new PatientIdentifierDataDefinition(null, patientIdentifierType);
        dd.setIncludeFirstNonNullOnly(true);
        if (converters.length > 0) {
            return new ConvertedPatientDataDefinition(dd, converters);
        }
        else {
            return dd;
        }
    }

    private PatientDataDefinition getMostRecentIdentifierOf(PatientIdentifierType patientIdentifierType, DataConverter... converters) {
        return getIdentifiersOf(patientIdentifierType,
                converters(new MostRecentlyCreatedConverter(PatientIdentifier.class), converters));
    }

    // Demographics

    @DocumentedDefinition("unknownPatient.value")
    public PatientDataDefinition getUnknownPatient() {
        PersonAttributeDataDefinition d = new PersonAttributeDataDefinition();
        d.setPersonAttributeType(emrApiProperties.getUnknownPatientPersonAttributeType());
        return convert(d, converters.getRawAttributeValue());
    }

    @DocumentedDefinition("preferredAddress.department")
    public PatientDataDefinition getPreferredAddressDepartment() {
        return getPreferredAddress("stateProvince");
    }

    @DocumentedDefinition("preferredAddress.commune")
    public PatientDataDefinition getPreferredAddressCommune() {
        return getPreferredAddress("cityVillage");
    }

    @DocumentedDefinition("preferredAddress.section")
    public PatientDataDefinition getPreferredAddressSection() {
        return getPreferredAddress("address3");
    }

    @DocumentedDefinition("preferredAddress.locality")
    public PatientDataDefinition getPreferredAddressLocality() {
        return getPreferredAddress("address1");
    }

    @DocumentedDefinition("preferredAddress.streetLandmark")
    public PatientDataDefinition getPreferredAddressStreetLandmark() {
        return getPreferredAddress("address2");
    }

    @DocumentedDefinition("registration.encounterDatetime")
    public PatientDataDefinition getRegistrationDatetime() {
        return getRegistrationEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("admission.location")
    public PatientDataDefinition getAdmissionLocation() {
        return getAdmissionEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("admission.encounterDatetime")
    public PatientDataDefinition getAdmissionDatetime() {
        return getAdmissionEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("lastEncounter.type")
    public PatientDataDefinition getLastEncounterType() {
        return getLastEncounter(new PropertyConverter(EncounterType.class, "encounterType"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("lastEncounter.location")
    public PatientDataDefinition getLastEncounterLocation() {
        return getLastEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("lastEncounter.encounterDatetime")
    public PatientDataDefinition getLastEncounterDatetime() {
        return getLastEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("checkin.location")
    public PatientDataDefinition getCheckinLocation() {
        return getCheckinEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("checkin.encounterDatetime")
    public PatientDataDefinition getCheckinDatetime() {
        return getCheckinEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("inpatient.location")
    public PatientDataDefinition getInpatientLocation() {
        return getAdmissionOrTransferEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("inpatient.encounterDatetime")
    public PatientDataDefinition getInpatientDatetime() {
        return getAdmissionOrTransferEncounter(new PropertyConverter(Encounter.class, "encounterDatetime"));
    }

    @DocumentedDefinition("registration.location")
    public PatientDataDefinition getRegistrationLocation() {
        return getRegistrationEncounter(new PropertyConverter(Encounter.class, "location"),
                new ObjectFormatter());
    }

    @DocumentedDefinition("registration.creator.name")
    public PatientDataDefinition getRegistrationCreatorName() {
        return getRegistrationEncounter(new PropertyConverter(Encounter.class, "creator"),
                new PropertyConverter(User.class, "personName"),
                new ObjectFormatter("{givenName} {familyName}"));
    }

    @DocumentedDefinition("registration.age")
    public PatientDataDefinition getRegistrationAge() {
        MappedData<PatientDataDefinition> effectiveDate = new MappedData<PatientDataDefinition>(getRegistrationDatetime(), null);

        AgeAtDateOfOtherDataDefinition ageAtRegistration = new AgeAtDateOfOtherDataDefinition();
        ageAtRegistration.setEffectiveDateDefinition(effectiveDate);

        return new ConvertedPatientDataDefinition(new PersonToPatientDataDefinition(ageAtRegistration), new AgeConverter("{y:1}"));
    }

    private PatientDataDefinition getPreferredAddress(String property) {
        return new ConvertedPatientDataDefinition(
                new PersonToPatientDataDefinition(
                        new PreferredAddressDataDefinition()),
                new PropertyConverter(PersonAddress.class, property));
    }

    private PatientDataDefinition getRegistrationEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition registrationEncounters = new EncountersForPatientDataDefinition();
        registrationEncounters.setTypes(Arrays.asList(Metadata.lookup(EncounterTypes.PATIENT_REGISTRATION)));

        return new ConvertedPatientDataDefinition(registrationEncounters,
                converters(new EarliestCreatedConverter(Encounter.class), converters));
    }
    private PatientDataDefinition getAdmissionEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition admissionEncounters = new EncountersForPatientDataDefinition();
        admissionEncounters.setTypes(Arrays.asList(Metadata.lookup(EncounterTypes.ADMISSION)));
        admissionEncounters.setOnlyInActiveVisit(true);
        admissionEncounters.setWhich(TimeQualifier.FIRST);
        return new ConvertedPatientDataDefinition(admissionEncounters, converters);
    }
    private PatientDataDefinition getAdmissionOrTransferEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition adtEncounters = new EncountersForPatientDataDefinition();
        adtEncounters.setTypes(Arrays.asList(Metadata.lookup(EncounterTypes.ADMISSION), Metadata.lookup(EncounterTypes.TRANSFER)));
        adtEncounters.setOnlyInActiveVisit(true);
        adtEncounters.setWhich(TimeQualifier.LAST);
        return new ConvertedPatientDataDefinition(adtEncounters,converters);
    }
    private PatientDataDefinition getCheckinEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition checkinEncounters = new EncountersForPatientDataDefinition();
        checkinEncounters.setTypes(Arrays.asList(Metadata.lookup(EncounterTypes.CHECK_IN)));
        checkinEncounters.setOnlyInActiveVisit(true);
        checkinEncounters.setWhich(TimeQualifier.FIRST);
        return new ConvertedPatientDataDefinition(checkinEncounters, converters);
    }

    private PatientDataDefinition getLastEncounter(DataConverter... converters) {
        EncountersForPatientDataDefinition visitEncounters = new EncountersForPatientDataDefinition();
        visitEncounters.setOnlyInActiveVisit(true);
        visitEncounters.setWhich(TimeQualifier.LAST);
        return new ConvertedPatientDataDefinition(visitEncounters, converters);
    }

    // Convenience methods

    protected ConvertedPatientDataDefinition convert(PatientDataDefinition d, DataConverter... converters) {
        return new ConvertedPatientDataDefinition(d, converters);
    }

    protected ConvertedPatientDataDefinition convert(PersonDataDefinition d, DataConverter... converters) {
        return convert(new PersonToPatientDataDefinition(d), converters);
    }
}
