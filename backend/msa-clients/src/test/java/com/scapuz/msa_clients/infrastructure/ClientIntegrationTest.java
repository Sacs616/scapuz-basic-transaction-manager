package com.scapuz.msa_clients.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scapuz.msa_clients.infrastructure.adapter.rest.dto.ClientRequest;
import com.scapuz.msa_clients.infrastructure.adapter.rest.dto.ClientResponse;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientIntegrationTest {

        @SuppressWarnings("resource")
        @Container
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                        .withDatabaseName("clients_db")
                        .withUsername("test")
                        .withPassword("test");

        @DynamicPropertySource
        static void configureProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.datasource.url", postgres::getJdbcUrl);
                registry.add("spring.datasource.username", postgres::getUsername);
                registry.add("spring.datasource.password", postgres::getPassword);
        }

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private static String createdClientId;

        @Test
        @Order(1)
        @DisplayName("Should create client successfully - Integration Test")
        void shouldCreateClientSuccessfully() throws Exception {
                ClientRequest request = ClientRequest.builder()
                                .identification("1700000000")
                                .name("John Doe")
                                .genre("M")
                                .birthDate(LocalDate.of(1990, 5, 15))
                                .address("Calle falsa 123")
                                .phone("+593987654321")
                                .build();

                MvcResult result = mockMvc.perform(post("/api/v1/clients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.clientId").exists())
                                .andExpect(jsonPath("$.name").value("John Doe"))
                                .andExpect(jsonPath("$.status").value("ACTIVE"))
                                .andReturn();

                ClientResponse response = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ClientResponse.class);
                createdClientId = response.getClientId().toString();
        }

        @Test
        @Order(2)
        @DisplayName("Should get client by ID - Integration Test")
        void shouldGetClientById() throws Exception {
                mockMvc.perform(get("/api/v1/clients/" + createdClientId))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.clientId").value(createdClientId))
                                .andExpect(jsonPath("$.name").value("John Doe"));
        }

        @Test
        @Order(3)
        @DisplayName("Should get all clients - Integration Test")
        void shouldGetAllClients() throws Exception {
                mockMvc.perform(get("/api/v1/clients"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @Order(4)
        @DisplayName("Should update client successfully - Integration Test")
        void shouldUpdateClientSuccessfully() throws Exception {
                ClientRequest updateRequest = ClientRequest.builder()
                                .identification("1700000000")
                                .name("Doe Updated")
                                .genre("M")
                                .birthDate(LocalDate.of(1990, 5, 15))
                                .address("Calle falsa 123")
                                .phone("+593987654321")
                                .build();

                mockMvc.perform(put("/api/v1/clients/" + createdClientId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Doe Updated"))
                                .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @Order(5)
        @DisplayName("Should return 404 for non-existent client - Integration Test")
        void shouldReturn404ForNonExistentClient() throws Exception {
                String nonExistentId = "00000000-0000-0000-0000-000000000000";

                mockMvc.perform(get("/api/v1/clients/" + nonExistentId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value(containsString("not found")));
        }

        @Test
        @Order(6)
        @DisplayName("Should return 400 for invalid client data - Integration Test")
        void shouldReturn400ForInvalidClientData() throws Exception {
                ClientRequest invalidRequest = ClientRequest.builder()
                                .name("John Doe")
                                .phone("+593987654321")
                                .birthDate(LocalDate.of(1990, 5, 15))
                                .build();

                mockMvc.perform(post("/api/v1/clients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.validationErrors").exists());
        }

        @Test
        @Order(7)
        @DisplayName("Should delete client successfully - Integration Test")
        void shouldDeleteClientSuccessfully() throws Exception {
                mockMvc.perform(delete("/api/v1/clients/" + createdClientId))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/v1/clients/" + createdClientId))
                                .andExpect(status().isNotFound());
        }
}