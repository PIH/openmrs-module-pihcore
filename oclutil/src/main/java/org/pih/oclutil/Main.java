package org.pih.oclutil;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class Main {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static String[] idsToGet = new String[] { "1423", "83531", "162586" };

    public static void main(String[] args) throws IOException {
        Client client = ClientBuilder.newClient();
        WebTarget cielConcepts = client.target("http://api.staging.openconceptlab.com/orgs/CIEL/sources/CIEL/concepts/");

        for (String id : idsToGet) {
            String response = cielConcepts.path(id)
                    .queryParam("includeMappings", "true")
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Token 44b43728766407b5a142ec864c240f9ac2a17cd5")
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
