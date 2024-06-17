package org.openmrs.module.pihcore.page.controller.admin;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Administrative tool to import fingerprints from a SQL Lite DB file
 */
public class ImportFingerprintsPageController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String REQUIRED_PRIVILEGE = "App: coreapps.systemAdministration";

    public void get(PageModel model) throws IOException {
        Context.requirePrivilege(REQUIRED_PRIVILEGE);
        model.addAttribute("fingerprintDbPath", "");
        model.addAttribute("existingRows", null);
        model.addAttribute("errorRows", null);
        model.addAttribute("successRows", null);
    }

    public void post(@RequestParam(value="fingerprintDbPath") String fingerprintDbPath, PageModel model, HttpServletRequest request) {
        model.addAttribute("fingerprintDbPath", fingerprintDbPath);
        model.addAttribute("existingRows", null);
        model.addAttribute("errorRows", null);
        model.addAttribute("successRows", null);
        if (!Context.hasPrivilege(REQUIRED_PRIVILEGE)) {
            request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, "You are not authorized to import fingerprints");
        }
        try {
            Map<String, List<Map<String, Object>>> results = importFromFile(fingerprintDbPath);
            for (String key : results.keySet()) {
                model.addAttribute(key, results.get(key));
            }
        }
        catch (Exception e) {
            log.error("Error importing fingerprints", e);
            if (request.isRequestedSessionIdValid()) {
                request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, e.getMessage());
            }
        }
    }

    protected Map<String, List<Map<String, Object>>> importFromFile(String fingerprintDbPath) throws Exception {
        if (StringUtils.isBlank(fingerprintDbPath)) {
            throw new IllegalStateException("Fingerprint DB Path is required");
        }
        File dbFile = new File(fingerprintDbPath);
        if (!dbFile.exists()) {
            throw new IllegalStateException(fingerprintDbPath + " does not exist");
        }
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        String query = "select hivemr, SubjectId, created_date from Subjects";
        Class.forName("org.sqlite.JDBC");

        List<Map<String, Object>> rows = new ArrayList<>();

        log.info("Reading fingerprints from DB...");
        try (Connection conn = DriverManager.getConnection(url)) {
            try (Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("identifier", rs.getString("hivemr"));
                        row.put("subjectId", rs.getString("SubjectId"));
                        row.put("createdDate", rs.getDate("created_date"));
                        rows.add(row);
                    }
                }
            }
        }
        log.info("Read " + rows.size() + " fingerprint rows");

        PatientIdentifierType hivEmrId = getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_HIVEMRV1_UUID);
        PatientIdentifierType fingerprintId = getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_BIOMETRICSREFERENCECODE_UUID);

        List<Map<String, Object>> existingRows = new ArrayList<>();
        List<Map<String, Object>> errorRows = new ArrayList<>();
        List<Map<String, Object>> successRows = new ArrayList<>();
        Map<String, List<Map<String, Object>>> ret = new HashMap<>();
        ret.put("existingRows", existingRows);
        ret.put("errorRows", errorRows);
        ret.put("successRows", successRows);

        int rowsProcessed = 0;
        for (Map<String, Object> row : rows) {

            // Find patient indicated by the given identifier
            String identifier = (String) row.get("identifier");
            String fingerprintSubjectId = (String) row.get("subjectId");
            Date createdDate = (Date) row.get("createdDate");

            List<PatientIdentifier> pis = getPatientService().getPatientIdentifiers(identifier, Collections.singletonList(hivEmrId), null, null, null);
            if (pis.isEmpty()) {
                pis = getPatientService().getPatientIdentifiers(identifier, null, null, null, null);
            }

            boolean alreadyExists = false;
            String error = null;

            if (pis.isEmpty()) {
                error = "No patient with identifier " + identifier + " found.";
            }
            else if (pis.size() > 1) {
                Set<Integer> patientIds = new HashSet<>();
                for (PatientIdentifier pi : pis) {
                    patientIds.add(pi.getPatient().getId());
                }
                if (patientIds.size() > 1) {
                    error = "Found multiple patients with identifier " + identifier + ". Patients: " + patientIds;
                }
            }

            if (error == null) {
                Patient patient = pis.get(0).getPatient();

                // Determine if they already have a fingerprint stored
                for (PatientIdentifier pi : patient.getActiveIdentifiers()) {
                    if (pi.getIdentifierType().equals(fingerprintId)) {
                        if (pi.getIdentifier().equals(fingerprintSubjectId)) {
                            alreadyExists = true;
                        } else {
                            error = "Patient already has an existing fingerprint, but with a different value.  Identifier id = " + pi.getPatientIdentifierId();
                        }
                    }
                }

                row.put("alreadyExists", alreadyExists);

                if (alreadyExists) {
                    existingRows.add(row);
                }
                else if (error != null) {
                    errorRows.add(row);
                }
                else {
                    try {
                        PatientIdentifier pi = new PatientIdentifier();
                        pi.setPatient(patient);
                        pi.setIdentifierType(fingerprintId);
                        pi.setIdentifier(fingerprintSubjectId);
                        pi.setDateCreated(createdDate);
                        getPatientService().savePatientIdentifier(pi);
                        successRows.add(row);
                    }
                    catch (Exception e) {
                        error = e.getMessage();
                        errorRows.add(row);
                    }
                }
            }
            else {
                errorRows.add(row);
            }
            row.put("error", error);
            if (error != null) {
                log.warn(row.toString());
            }
            else {
                log.debug(row.toString());
            }

            rowsProcessed++;

            if (rowsProcessed % 20 == 0) {
                log.info("Processed " + rowsProcessed + " / " + rows.size() + " rows");
                Context.flushSession();
                Context.clearSession();
            }
        }

        log.info("Existing Rows: " + existingRows.size());
        log.info("Error Rows: " + errorRows.size());
        log.info("Success Rows: " + successRows.size());

        return ret;
    }

    public PatientService getPatientService() {
        return Context.getPatientService();
    }
}