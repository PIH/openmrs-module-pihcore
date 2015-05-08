package org.openmrs.module.pihcore.reporting.library;

import org.openmrs.PersonAttribute;
import org.openmrs.module.pihcore.metadata.Metadata;
import org.openmrs.module.pihcore.metadata.core.PersonAttributeTypes;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.definition.library.BaseDefinitionLibrary;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.springframework.stereotype.Component;

@Component
public class PihPersonDataLibrary extends BaseDefinitionLibrary<PersonDataDefinition> {

    @Override
    public Class<? super PersonDataDefinition> getDefinitionType() {
        return PersonDataDefinition.class;
    }

    @Override
    public String getKeyPrefix() {
        return "mirebalais.personDataCalculation.";
    }

    @DocumentedDefinition("telephoneNumber")
    public PersonDataDefinition getTelephoneNumber() {
        return new ConvertedPersonDataDefinition("telephoneNumber.value",
                new PersonAttributeDataDefinition(Metadata.lookup(PersonAttributeTypes.TELEPHONE_NUMBER)),
                new PropertyConverter(PersonAttribute.class, "value"));
    }
}
