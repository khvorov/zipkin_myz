package com.khvorov.zipkin.myz.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by sammy on 3/19/16.
 */
public class ClientStarter {
    public static void main(final String[] args) throws Exception {
        final Client client = ClientBuilder
                .newClient()
                .register(new ZipkinRequestFilter("People", null))
                .register(new ZipkinResponseFilter("People", null));

        final Response response = client
                .target("http://localhost:8080/rest/api/people")
                .request(MediaType.APPLICATION_JSON)
                .get();

        if (response.getStatus() == 200) {
            System.out.println(response.readEntity(String.class));
        }

        response.close();
        client.close();

        Thread.sleep(1000);
    }
}
