package com.db.alerttriage.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class AlertControllerIntegrationTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");


    @Autowired
    private MockMvc mockMvc;


    @Test
    void validAlertReturns201() throws Exception {

        String requestBody= """
                {
                  "sourceAlertId": "scan-api-1",
                  "cveId": "CVE-2026-1234",
                  "hostname": "router-api-01",
                  "cvssScore": 9.1,
                  "description": "Critical vulnerability",
                  "detectedAt": "2026-08-25T00:00:00Z"
               }
           """;
        mockMvc.perform(
                post("/api/alerts")
                        .contentType("application/json")
                        .content(requestBody)
        ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.severity").value("CRITICAL"));
    }


    @Test
    void nvalidCvssScore() throws Exception {

         String requestBody= """
                {
                  "sourceAlertId": "scan-api-1",
                  "cveId": "CVE-2026-1234",
                  "hostname": "router-api-01",
                  "cvssScore": 99,
                  "description": "Critical vulnerability",
                  "detectedAt": "2026-08-25T00:00:00Z"
               }
           """;

         mockMvc.perform(
                 post("/api/alerts")
                         .contentType("application/json")
                         .content(requestBody)
         ).andExpect(status().isBadRequest());

    }

    @Test
    void getAlertsReturnsNewestFirst() throws Exception {

        String firstAlert = """
            {
              "sourceAlertId": "scan-api-3",
              "cveId": "CVE-2026-3001",
              "hostname": "router-api-03",
              "cvssScore": 5.0,
              "description": "First alert",
              "detectedAt": "2026-08-25T00:00:00Z"
            }
            """;

        String secondAlert = """
            {
              "sourceAlertId": "scan-api-4",
              "cveId": "CVE-2026-3002",
              "hostname": "router-api-04",
              "cvssScore": 8.0,
              "description": "Second alert",
              "detectedAt": "2026-08-25T01:00:00Z"
            }
            """;

        mockMvc.perform(
                post("/api/alerts")
                        .contentType("application/json")
                        .content(firstAlert)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post("/api/alerts")
                        .contentType("application/json")
                        .content(secondAlert)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                        get("/api/alerts")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cveId").value("CVE-2026-3002"))
                .andExpect(jsonPath("$[1].cveId").value("CVE-2026-3001"));
    }


}
