package org.openmrs.module.pihcore.action;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.paperrecord.PaperRecordConstants;
import org.openmrs.module.pihcore.metadata.haiti.PihHaitiPatientIdentifierTypes;
import org.openmrs.module.registrationapp.action.AfterPatientCreatedAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("assignDossierNumber")
public class AssignDossierNumber implements AfterPatientCreatedAction {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdentifierSourceService identifierSourceService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private PatientService patientService;

    @Override
    public synchronized void afterPatientCreated(Patient patient, Map<String, String[]> map) {

        PatientIdentifierType dossierIdentifierType = MetadataUtils.existing(PatientIdentifierType.class, PihHaitiPatientIdentifierTypes.DOSSIER_NUMBER.uuid());
        Location medicalRecordLocation = getMedicalRecordLocation();

        String dossierId = "";

        dossierId = identifierSourceService.generateIdentifier(dossierIdentifierType, medicalRecordLocation,
                "generating a new dossier number");

        // double check to make sure this identifier is not in use--since manual entry is allowed, it could be
        while (dossierIdentifierInUse(dossierId, dossierIdentifierType, medicalRecordLocation)) {
            log.error("Attempted to generate duplicate dossier identifier " + dossierId );
            dossierId = identifierSourceService.generateIdentifier(dossierIdentifierType, medicalRecordLocation,
                    "generating a new dossier number");
        }


        if (StringUtils.isBlank(dossierId)) {
            throw new APIException("Unable to generate dossier number for patient " + patient);
        }

        PatientIdentifier dossierIdentifier = new PatientIdentifier(dossierId, dossierIdentifierType,
                medicalRecordLocation);
        patient.addIdentifier(dossierIdentifier);
        patientService.savePatientIdentifier(dossierIdentifier);

    }


    // assumption here is that there is only one medical record location
    private Location getMedicalRecordLocation() {

        Location medicalRecordLocation = null;

        for (Location l : locationService.getAllLocations(false)) {
            if (l.hasTag(PaperRecordConstants.LOCATION_TAG_MEDICAL_RECORD_LOCATION)) {
                if (medicalRecordLocation == null) {
                    medicalRecordLocation = l;
                }
                else {
                    throw new IllegalStateException("Only one location can be tagged as medical record location if using Assign Dossier Number action");
                }
            }
        }

        if (medicalRecordLocation == null) {
            throw new IllegalStateException("No location tagged as medical record location");
        }
        return medicalRecordLocation;
    }

    private boolean dossierIdentifierInUse(String identifier, PatientIdentifierType dossierIdentifierType, Location medicalRecordLocation) {
        List<PatientIdentifier> identifiers = patientService.getPatientIdentifiers(identifier,
                Collections.singletonList(dossierIdentifierType), Collections.singletonList(medicalRecordLocation), null, null);

        return identifiers != null && identifiers.size() > 0 ? true : false;
    }
}
