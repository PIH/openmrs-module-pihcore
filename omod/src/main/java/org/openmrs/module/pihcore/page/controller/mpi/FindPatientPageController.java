package org.openmrs.module.pihcore.page.controller.mpi;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.emr.EmrConstants;
import org.openmrs.module.emr.utils.GeneralUtils;
import org.openmrs.module.importpatientfromws.RemotePatient;
import org.openmrs.module.importpatientfromws.api.ImportPatientFromWebService;
import org.openmrs.module.pihcore.ZlConfigConstants;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPatientPageController {

    public static final String MPI_REMOTE_SERVER = "lacolline";
    public static final String MPI_SEARCH_RESULTS="mpiSearchResults";
    public static final Integer MPI_CONNECT_TIMEOUT = 60000;

    private final Log log = LogFactory.getLog(getClass());

    private void saveToCache(List<RemotePatient> remotePatients, HttpSession session) {
        Map<String, RemotePatient> mpiSearchResults = (Map<String, RemotePatient>) session.getAttribute(MPI_SEARCH_RESULTS);
        if (mpiSearchResults == null) {
            mpiSearchResults = new HashMap<String, RemotePatient>();
            session.setAttribute(MPI_SEARCH_RESULTS, mpiSearchResults);
        }
        if(remotePatients!=null && remotePatients.size()>0){
            for(RemotePatient remotePatient : remotePatients){
                mpiSearchResults.put(MPI_REMOTE_SERVER + ":" + remotePatient.getRemoteUuid(), remotePatient);
            }
        }
    }

    private RemotePatient getFromCache(String uuid, HttpSession session){

        RemotePatient remotePatient = null;
        Map<String, RemotePatient> mpiSearchResults = (Map<String, RemotePatient>) session.getAttribute(MPI_SEARCH_RESULTS);
        if(mpiSearchResults!=null && mpiSearchResults.size()>0){
            remotePatient = mpiSearchResults.get(MPI_REMOTE_SERVER + ":" + uuid);
        }
        return remotePatient;
    }

    private RemotePatient removeFromCache(String uuid, HttpSession session){

        RemotePatient remotePatient = null;
        Map<String, RemotePatient> mpiSearchResults = (Map<String, RemotePatient>) session.getAttribute(MPI_SEARCH_RESULTS);
        if(mpiSearchResults!=null && mpiSearchResults.size()>0){
            String key = MPI_REMOTE_SERVER + ":" + uuid;
            remotePatient = mpiSearchResults.get(key);
            if(remotePatient!=null){
                mpiSearchResults.remove(key);
            }
        }
        return remotePatient;
    }

    public void get(PageModel model,
                    @SpringBean ImportPatientFromWebService webService,
                    @RequestParam(required = false, value="id") String id,
                    @RequestParam(required = false, value = "name") String name,
                    @RequestParam(required = false, value = "gender") String gender,
                    UiUtils ui,
                    HttpSession session) {

        List<RemotePatient> results = null;
        try {
            if (StringUtils.isNotBlank(id)) {
                results = webService.searchRemoteServer(MPI_REMOTE_SERVER, id, MPI_CONNECT_TIMEOUT);
            }else if(StringUtils.isNotBlank(name)){
                results = webService.searchRemoteServer(MPI_REMOTE_SERVER, name, gender, null, MPI_CONNECT_TIMEOUT);
            }
            if(results!=null && results.size()>0){
                saveToCache(results, session);
                model.addAttribute("addressHierarchyLevels", GeneralUtils.getAddressHierarchyLevels());
            }
        } catch (Exception e) {
            log.error("Error communicating with remote server", e);
            session.setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui.message("mirebalais.mpi.connect.error", e.getMessage()));
        }

        model.addAttribute("results", results);
    }

    public String post(@RequestParam(required=false, value="remoteServer") String remoteServer,
                       @RequestParam("remoteUuid") String remoteUuid,
                       UiUtils ui,
                       HttpServletRequest request
                       ) {

        if(StringUtils.isNotBlank(remoteUuid)){
            HttpSession session = request.getSession();
            RemotePatient remotePatient = getFromCache(remoteUuid, session);
            if(remotePatient!=null){
                //import the patient
                try{
                    Patient patient = remotePatient.getPatient();
                    PatientIdentifierType zlIdentifierType = Context.getPatientService().getPatientIdentifierTypeByUuid(ZlConfigConstants.PATIENTIDENTIFIERTYPE_ZLEMRID_UUID);
                    if(zlIdentifierType!=null && patient!=null){
                        PatientIdentifier patientIdentifier = patient.getPatientIdentifier(zlIdentifierType);
                        if(patientIdentifier!=null){
                            patientIdentifier.setPreferred(true);
                        }
                    }
                    patient = Context.getPatientService().savePatient(patient);
                    if(patient!=null){
                        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_INFO_MESSAGE, ui.message("mirebalais.mpi.import.success", ui.format(patient)));
                        request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");
                        removeFromCache(remoteUuid, session);
                        return "redirect:" + ui.pageLink("coreapps", "patientdashboard/patientDashboard?patientId=" + patient.getId().toString());
                    }
                }catch(Exception e){
                    log.error("failed to import patient", e);
                    request.getSession().setAttribute(EmrConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui.message("mirebalais.mpi.import.error", e.getMessage()));
                }

            }
        }
        return "redirect:" + ui.pageLink("mirebalais/mpi", "findPatient");

    }
}
