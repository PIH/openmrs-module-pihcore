package org.openmrs.module.pihcore.web;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.AdministrationService;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Integrates with Angular Translate, simply initialize as follows:
 * $translateProvider.useUrlLoader('/' + OPENMRS_CONTEXT_PATH + '/module/pihcore/messages.json')
 */
@Controller
public class MessagesController {

    @Autowired
    private MessageSourceService messageSourceService;

    @Autowired
    @Qualifier("adminService")
    private AdministrationService administrationService;

    private String eTag;

    private Map<String, Map<String,String>> messages;

    @RequestMapping(value = "/module/pihcore/messages.json", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String,String>> getMessages(@RequestParam("lang") String lang, WebRequest webRequest) {

        // TODO zip?
        // TODO how do we do a refresh on context refreshed (or are controllers refreshed by default?)
        // TODO move into uicommons?  what modules need updating (uicommons, pihcore?)
        // TODO fix angular-tranlate-url-loader so that it doesn't used my hacked version
        // TODO properly handle fallbacks to default locales

        // create the translation map if we haven't already
        if (messages == null) {
            initializeMessages();
        }

        // just take the lang component if we happen to have both
        if (lang.contains("_")) {
            lang = lang.split("_")[0];
        }

        String eTagFromClient = webRequest.getHeader("If-None-Match");

        // see if this client already has the right version cached, if so send back not modified
        if (eTagFromClient != null && eTagFromClient.contains(eTag)) {
            return new ResponseEntity<Map<String, String>>(new HashMap<String, String>(), HttpStatus.NOT_MODIFIED);
        }
        // otherwise set eTag and return the codes requested
        else {
            HttpHeaders headers = new HttpHeaders();
            headers.setETag(eTag);
            return new ResponseEntity<Map<String, String>>(messages.get(lang), headers, HttpStatus.OK);
        }

    }

    private void initializeMessages() {

        messages = new HashMap<String, Map<String, String>>();

        for (PresentationMessage message : messageSourceService.getPresentations()) {
            // TODO in the future we may want to start using the full locale code if 1) we start having country-specific
            // TODO language files, and 2) we start supporting country components on the client side
            String lang = message.getLocale().getLanguage();
            if (messages.get(lang) == null) {
                messages.put(lang, new HashMap<String, String>());
            }
            messages.get(lang).put(message.getCode(), message.getMessage());
        }

        eTag = UUID.randomUUID().toString();
    }

}
