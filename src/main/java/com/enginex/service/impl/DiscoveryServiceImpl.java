package com.enginex.service.impl;

import com.enginex.model.DiscoveryRequest;
import com.enginex.model.DiscoveryResponse;
import com.enginex.service.DiscoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DiscoveryServiceImpl implements DiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryServiceImpl.class);

    static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();


    private String resolveDiscoveryServiceAdapter() {
        final String discoveryServiceAdapter = System.getProperty("discovery.service.adapter");
        if (discoveryServiceAdapter == null) {
            LOGGER.warn("Discovery service adapter is null. Now setting it to 'default'");
            return "default";
        }
        return discoveryServiceAdapter;
    }


    @Override
    public String getResourceUtl(String url) {

        final DiscoveryRequest discoveryRequest = new DiscoveryRequest();
        discoveryRequest.setUrl(url);
        discoveryRequest.setAdapterName(resolveDiscoveryServiceAdapter());

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
