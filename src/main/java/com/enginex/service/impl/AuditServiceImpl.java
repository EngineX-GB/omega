package com.enginex.service.impl;

import com.enginex.model.*;
import com.enginex.service.AuditService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AuditServiceImpl implements AuditService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public AuditResponseCode isDuplicate(final Link link) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8083/api/v1/audit?sourceUrl="
                        + URLEncoder.encode(link.getUrl(), StandardCharsets.UTF_8.toString())))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                final List<AuditRecordResponse> auditRecordResponse = MAPPER.readValue(response.body(), new TypeReference<>(){});
                if (auditRecordResponse != null && auditRecordResponse.isEmpty()) {
                    return AuditResponseCode.NON_DUPLICATE;
                }
                return AuditResponseCode.DUPLICATE;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return AuditResponseCode.ERROR;
    }

    @Override
    public AuditResponseCode update(Link link) {
        final AuditUpdateRequest auditUpdateRequest = new AuditUpdateRequest();
        auditUpdateRequest.setSourceUrl(link.getUrl());
        auditUpdateRequest.setStrategy(link.getStrategyType().name());
        auditUpdateRequest.setSystemFilePath(link.getFilename());
        try {
            final String requestString = MAPPER.writeValueAsString(auditUpdateRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8083/api/v1/audit/add"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestString))
                    .build();
            final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                final AuditUpdateResponse auditUpdateResponse = MAPPER.readValue(response.body(), new TypeReference<>(){});
                if (auditUpdateResponse != null && auditUpdateResponse.getMessage().equalsIgnoreCase("SUCCESS")) {
                    return AuditResponseCode.UPDATED;
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return AuditResponseCode.ERROR;
    }

}
