package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.User;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.reporting.common.AuditInfo;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.AttributeValueConverter;
import org.openmrs.module.reporting.data.converter.ChainedConverter;
import org.openmrs.module.reporting.data.converter.CollectionConverter;
import org.openmrs.module.reporting.data.converter.CollectionElementConverter;
import org.openmrs.module.reporting.data.converter.CountConverter;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.DataSetRowConverter;
import org.openmrs.module.reporting.data.converter.ListConverter;
import org.openmrs.module.reporting.data.converter.NullValueConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.ObsFromObsGroupConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataConverterLibrary {

    @Autowired
    EmrApiProperties emrApiProperties;

    // Patient

    public DataConverter getAgeToOneDecimalPlaceConverter() {
        return new AgeConverter(AgeConverter.YEARS_TO_ONE_DECIMAL_PLACE);
    }

    // Address

    public DataConverter getAddressComponent(String propertyName) {
        return new PropertyConverter(PersonAddress.class, propertyName);
    }

    // Patient Identifier

    public DataConverter getIdentifierConverter() {
        return new PropertyConverter(PatientIdentifier.class, "identifier");
    }

    public DataConverter getIdentifierLocationNameConverter() {
        return new ChainedConverter(new PropertyConverter(PatientIdentifier.class, "location"), getObjectFormatter());
    }

    // Person Attribute

    public DataConverter getRawAttributeValue() {
        return new PropertyConverter(PersonAttribute.class, "value");
    }

    public DataConverter getHydratedAttributeValue(PersonAttributeType type) {
        return new AttributeValueConverter(type);
    }

    // Encounter

    public DataConverter getEncounterDatetimeConverter() {
        return new PropertyConverter(Encounter.class, "encounterDatetime");
    }

    public DataConverter getEncounterLocationNameConverter() {
        return new ChainedConverter(new PropertyConverter(Encounter.class, "location"), getObjectFormatter());
    }

    public DataConverter getEncounterTypeNameConverter() {
        return new ChainedConverter(new PropertyConverter(Encounter.class, "type"), getObjectFormatter());
    }

    // Obs

    public DataConverter getObsDatetimeConverter() {
        return new PropertyConverter(Obs.class, "obsDatetime");
    }

    public DataConverter getObsValueNumericConverter() {
        return new PropertyConverter(Obs.class, "valueNumeric");
    }

    public DataConverter getObsValueDatetimeConverter() {
        return new PropertyConverter(Obs.class, "valueDatetime");
    }

    public DataConverter getObsValueCodedConverter() {
        return new PropertyConverter(Obs.class, "valueCoded");
    }

    public DataConverter getObsValueCodedNameConverter() {
        return new ChainedConverter(getObsValueCodedConverter(), getObjectFormatter());
    }

    public DataConverter getObsValueTextConverter() {
        return new PropertyConverter(Obs.class, "valueText");
    }

    public DataConverter getObsValueCodedPresentConverter(Concept valueCoded) {
        ChainedConverter converter = new ChainedConverter();
        converter.addConverter(new CollectionConverter(getObsValueCodedConverter(), false, null));
        converter.addConverter(new CollectionElementConverter(valueCoded, true, false));
        return converter;
    }

    public DataConverter getObsFromObsGroupConverter(Concept concept) {
        return new ObsFromObsGroupConverter(concept);
    }

    // Program Enrollments

    public DataConverter getProgramEnrollmentDateConverter() {
        return new PropertyConverter(PatientProgram.class, "dateEnrolled");
    }

    public DataConverter getStateNameConverter() {
        return new ChainedConverter(new PropertyConverter(PatientState.class, "state.concept"), getObjectFormatter());
    }

    public DataConverter getStateStartDateConverter() {
        return new PropertyConverter(PatientState.class, "startDate");
    }

    public DataConverter getStateLocationConverter() {
        return new ChainedConverter(new PropertyConverter(PatientState.class, "patientProgram.location"), getObjectFormatter());
    }

    public DataConverter getStateProgramEnrollmentDateConverter() {
        return new PropertyConverter(PatientState.class, "patientProgram.dateEnrolled");
    }

    // User and Person

    public DataConverter getPersonNameConverter() {
        return new ObjectFormatter("{givenName} {familyName}");
    }

    public DataConverter getPersonAsNameConverter() {
        return new ChainedConverter(new PropertyConverter(Person.class, "personName"), getPersonNameConverter());
    }

    public DataConverter getUserAsNameConverter() {
        return new ChainedConverter(new PropertyConverter(User.class, "personName"), getPersonNameConverter());
    }

    // General Converters

    public DataConverter getObjectFormatter() {
        return new ObjectFormatter();
    }

    // Audit Info

    public DataConverter getAuditInfoCreatorNameConverter() {
        return new ChainedConverter(new PropertyConverter(AuditInfo.class, "creator"), getUserAsNameConverter());
    }

    public DataConverter getAuditInfoDateCreatedConverter() {
        return new PropertyConverter(AuditInfo.class, "dateCreated");
    }

    // List and Collection Converters

    public DataConverter getCollectionSizeConverter() {
        return new CountConverter();
    }

    public DataConverter getListItemConverter(Integer index, DataConverter... converters) {
        ChainedConverter ret = new ChainedConverter();
        ret.addConverter(new ListConverter(index, Object.class));
        for (DataConverter converter : converters) {
            ret.addConverter(converter);
        }
        return ret;
    }

    public DataConverter getLastListItemConverter(DataConverter... converters) {
        ChainedConverter ret = new ChainedConverter();
        ret.addConverter(new ListConverter(TimeQualifier.LAST, 1, Object.class));
        for (DataConverter converter : converters) {
            ret.addConverter(converter);
        }
        return ret;
    }

    public DataConverter getDataSetItemConverter(Integer index, String columnName, Object nullReplacement) {
        ChainedConverter ret = new ChainedConverter();
        ret.addConverter(new ListConverter(index, DataSetRow.class));
        ret.addConverter(new DataSetRowConverter(columnName));
        if (nullReplacement != null) {
            ret.addConverter(new NullValueConverter(nullReplacement));
        }
        return ret;
    }

    public DataConverter getLastDataSetItemConverter(String columnName, Object nullReplacement) {
        ChainedConverter ret = new ChainedConverter();
        ret.addConverter(new ListConverter(TimeQualifier.LAST, 1, DataSetRow.class));
        ret.addConverter(new DataSetRowConverter(columnName));
        if (nullReplacement != null) {
            ret.addConverter(new NullValueConverter(nullReplacement));
        }
        return ret;
    }
}
