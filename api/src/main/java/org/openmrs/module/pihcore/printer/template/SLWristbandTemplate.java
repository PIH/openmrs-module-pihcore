package org.openmrs.module.pihcore.printer.template;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.addresshierarchy.AddressHierarchyLevel;
import org.openmrs.module.addresshierarchy.service.AddressHierarchyService;
import org.openmrs.module.addresshierarchy.util.AddressHierarchyUtil;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.adt.AdtService;
import org.openmrs.module.paperrecord.PaperRecordProperties;
import org.openmrs.module.pihcore.PihEmrConfigConstants;
import org.openmrs.module.pihcore.SierraLeoneConfigConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Template for the wristbands we print at MCOE.
 * Created the code in ZPL II language to send to the wristband printer
 */
@Component("slWristbandTemplate")
public class SLWristbandTemplate {

    private final Log log = LogFactory.getLog(getClass());
    public static Boolean LOWEST_LEVEL_ON_SEPARATE_LINE = true;

    public static Boolean SKIP_HIGHEST_LEVEL = true;

    @Autowired
    private AdtService adtService;

    @Autowired
    private MessageSourceService messageSourceService;

    @Autowired
    private EmrApiProperties emrApiProperties;

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;



    // TODO figure out why this isn't getting autowired properly (at least for tests)
    //@Autowired
    private AddressHierarchyService addressHierarchyService;

    /**
     * Setters are just here to insert mocks during testing
     */
    protected void setAdtService(AdtService adtService) {
        this.adtService = adtService;
    }

    protected void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    protected void setEmrApiProperties(EmrApiProperties emrApiProperties) {
        this.emrApiProperties = emrApiProperties;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void setAddressHierarchyService(AddressHierarchyService addressHierarchyService) {
        this.addressHierarchyService = addressHierarchyService;
    }

    public String generateWristband(Patient patient, Location location, Locale locale) {
        StringBuffer data = new StringBuffer();

        if (addressHierarchyService == null) {
            addressHierarchyService = Context.getService(AddressHierarchyService.class);
        }
        Locale printerLocale = (locale != null) ? locale : new Locale("en"); // default to English
        DateFormat fullDate  = new SimpleDateFormat("dd-MMM-yyyy", printerLocale);

        data.append("^XA");
        data.append("^CI28");   // specify Unicode encoding
        data.append("^MTD");    // set direct transfer type
        data.append("^FWB");    // set orientation

        // visit location & current date
        Location visitLocation = adtService.getLocationThatSupportsVisits((org.openmrs.Location) location);
        String visitLocationDisplay = "";
        if (visitLocation != null) {
            visitLocationDisplay = visitLocation.getUuid().equals(SierraLeoneConfigConstants.LOCATION_KGH_UUID) ? "Koidu Government Hospital" : visitLocation.getName();
        }

        data.append("^FO050,200^FB1650,1,0,L,0^AS^FD" + visitLocationDisplay + " "
                + fullDate.format(new Date()) + "^FS");

        PatientIdentifier primaryIdentifier = patient.getPatientIdentifier(emrApiProperties.getPrimaryIdentifierType());
        // patient name: for now, only printing given and family names
        String patientName = null;

        if(patient.getPersonName() != null ){
            patientName = (patient.getPersonName().getGivenName() != null ? patient.getPersonName().getGivenName() : "") + " "
                    + (patient.getPersonName().getFamilyName() != null ? patient.getPersonName().getFamilyName() : "");
        }

        data.append("^FO100,200^FB1650,1,0,L,0^AT^FD" + patientName + "  " + primaryIdentifier.getIdentifier() + "^FS");

        // gender
        data.append("^FO150,200^FB1650,1,0,L,0^AS^FD" + messageSourceService.getMessage("coreapps.gender." + patient.getGender(), null, printerLocale) + "^FS");

        if (patient.getBirthdate() != null) {
            if (!patient.getBirthdateEstimated()) {
                 data.append("^FO150,200^FB1350,1,0,L,0^AS^FD" + fullDate.format(patient.getBirthdate()) +  "^FS");
            }
            else {
                data.append("^FO150,200^FB1350,1,0,L,0^AS^FD" + messageSourceService.getMessage("pihcore.date_estimated", null, printerLocale) + " " + patient.getAge() + " " + messageSourceService.getMessage("pihcore.units.years", null, printerLocale) + "^FS");
            }

        }

        // telephone number
        PersonAttributeType telephoneNumberAttributeType = personService.getPersonAttributeTypeByUuid(PihEmrConfigConstants.PERSONATTRIBUTETYPE_TELEPHONE_NUMBER_UUID);
        if (telephoneNumberAttributeType != null) {
            String telephoneNumber = patient.getAttribute(telephoneNumberAttributeType) != null ? patient.getAttribute(telephoneNumberAttributeType).getValue() : null;
            if (StringUtils.isNotBlank(telephoneNumber)) {
                data.append("^FO190,200^FB1650,1,0,L,0^AS^FD" + telephoneNumber + "^FS");
            }
        }

        // national identification number
        PatientIdentifierType nationalIdNumberType = patientService.getPatientIdentifierTypeByUuid(SierraLeoneConfigConstants.PATIENTIDENTIFIERTYPE_NATIONALID_UUID);
        if (nationalIdNumberType != null) {
            PatientIdentifier nationalIdNumber = patient.getPatientIdentifier(nationalIdNumberType);
            if (nationalIdNumber != null) {
                data.append("^FO190,200^FB1350,1,0,L,0^AS^FDNIN: " + nationalIdNumber.getIdentifier() + "^FS");
            }
        }

        // address (based on address hierarchy)
        PersonAddress address = patient.getPersonAddress();
        AddressHierarchyLevel level = addressHierarchyService.getBottomAddressHierarchyLevel();
        int numberOfLevels = addressHierarchyService.getAddressHierarchyLevelsCount();

        if (address != null && numberOfLevels > 0) {

            int levelCount = 1;

            if (LOWEST_LEVEL_ON_SEPARATE_LINE) {
                String lowestLevelStr = AddressHierarchyUtil.getAddressFieldValue(address, level.getAddressField());
                if (StringUtils.isNotBlank(address.getAddress2())) {
                    data.append("^FO230,200^FB1650,1,0,L,0^AS^FD" + lowestLevelStr + "^FS");
                }
                levelCount++;
            }

            StringBuffer addressStr = new StringBuffer();

            while (levelCount < numberOfLevels || (!SKIP_HIGHEST_LEVEL && levelCount <= numberOfLevels)
                    && level.getParent() != null) {  // level.getParent() should never equal null as long as levelCount <= numberOfLevels, but just to be safe we will check

                level = level.getParent();

                String levelStr = AddressHierarchyUtil.getAddressFieldValue(address, level.getAddressField());

                if (StringUtils.isNotBlank(levelStr)) {
                    addressStr.append(levelStr + ", ");
                }

                levelCount++;
            }

            if (StringUtils.isNotBlank(addressStr.toString())) {
                // trim off trailing comma and space
                addressStr.delete(addressStr.length() - 2, addressStr.length());
                data.append("^FO270,200^FB1650,1,0,L,0^AS^FD" + addressStr.toString() + "^FS");
            }
        }

        // barcode with primary identifier
        if (primaryIdentifier != null) {
            data.append("^FO100,1900^AT^BY4^BC,150,N^FD" + primaryIdentifier.getIdentifier() + "^XZ");
        }
        return data.toString();
    }
}
