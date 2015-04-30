package org.pih.oclutil;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final String PRODUCTION = "http://api.openconceptlab.org/orgs/CIEL/sources/CIEL/concepts/";
    public static final String STAGING = "http://api.staging.openconceptlab.com/orgs/CIEL/sources/CIEL/concepts/";
    public static final String DEV = "http://api.dev.openconceptlab.com/orgs/CIEL/sources/CIEL/concepts/";

    private static String server = PRODUCTION;

    private static String[] idsToGet = new String[] { "162394" };

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String, String> tokens = new HashMap<String, String>();
    static {
        tokens.put(DEV, "0e90e12e98b87a3d45a58d9a79c004022672c31d");
        tokens.put(STAGING, "44b43728766407b5a142ec864c240f9ac2a17cd5");
        tokens.put(PRODUCTION, "not checking this in");
    }

    public static void main(String[] args) throws IOException {
        Client client = ClientBuilder.newClient();
        String url = server;
        WebTarget cielConcepts = client.target(url);

        String token = tokens.get(server);
        if (token == null) {
            throw new IllegalStateException("Can't find token for " + server);
        }
        for (String id : idsToGet) {
            String response = cielConcepts.path(id)
                    .queryParam("includeMappings", "true")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Token " + token)
                    .get(String.class);

//            System.out.println("Raw response => ");
//            System.out.println(response);
//            System.out.println();

            OclConcept concept = objectMapper.readValue(response, OclConcept.class);

            System.out.println("// " + concept);
            System.out.println(concept.codeForBuilder(id));
            System.out.println();
        }
    }

}
