package com.enginex.service.impl;

import com.enginex.model.DiscoveryRequest;
import com.enginex.model.DiscoveryResponse;
import com.enginex.service.DiscoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscoveryServiceImpl implements DiscoveryService {

    static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Override
    public String getResourceUtl(String url) {
        final DiscoveryRequest discoveryRequest = new DiscoveryRequest();
        discoveryRequest.setUrl(url);
        discoveryRequest.setAdapterName("default");

        try {
            final String requestString = MAPPER.writeValueAsString(discoveryRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8082/api/v1/discovery/link"))
                    .header("Content-Type", "application/json")
                    .method("GET", HttpRequest.BodyPublishers.ofString(requestString))
                    .build();
            final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                DiscoveryResponse discoveryResponse = MAPPER.readValue(response.body(), DiscoveryResponse.class);
                if (discoveryResponse != null && discoveryResponse.getLink() != null) {
                    return discoveryResponse.getLink();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "NULL_LINK_RETURNED";
    }
}
