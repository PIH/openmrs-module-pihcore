package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.haiticore.metadata.HaitiPersonAttributeTypes;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredAddressDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PihPersonDataLibrary extends BaseDefinitionLibrary<PersonDataDefinition> {

    @Autowired
    EmrApiProperties emrApiProperties;

    @Autowired
    DataConverterLibrary converters;

    @Override
    public Class<? super PersonDataDefinition> getDefinitionType() {
        return PersonDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "mirebalais.personDataCalculation.";
    }

    @DocumentedDefinition("birthdate")
    public PersonDataDefinition getBirthdate() {
        return convert(new BirthdateDataDefinition(), new PropertyConverter(Birthdate.class, "birthdate"));
    }

    @DocumentedDefinition("telephoneNumber")
    public PersonDataDefinition getTelephoneNumber() {
        PersonAttributeDataDefinition d = new PersonAttributeDataDefinition();
        d.setPersonAttributeType(emrApiProperties.getTelephoneAttributeType());
        return convert("telephoneNumber.value", d, converters.getRawAttributeValue());
    }

    public PersonDataDefinition getMothersFirstName() {
        PersonAttributeDataDefinition d = new PersonAttributeDataDefinition();
        d.setPersonAttributeType(Metadata.lookup(HaitiPersonAttributeTypes.MOTHERS_FIRST_NAME));
        return convert(d, converters.getRawAttributeValue());
    }

    @DocumentedDefinition
    public PersonDataDefinition getPreferredAddress() {
        return new PreferredAddressDataDefinition();
    }

    // Convenience methods

    protected ConvertedPersonDataDefinition convert(String name, PersonDataDefinition d, DataConverter... converters) {
        return new ConvertedPersonDataDefinition(name, d, converters);
    }

    protected ConvertedPersonDataDefinition convert(PersonDataDefinition d, DataConverter... converters) {
        return convert(null, d, converters);
    }
}
