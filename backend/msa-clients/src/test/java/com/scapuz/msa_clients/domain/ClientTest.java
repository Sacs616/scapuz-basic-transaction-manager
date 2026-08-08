package com.scapuz.msa_clients.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.scapuz.msa_clients.domain.exception.DomainException;
import com.scapuz.msa_clients.domain.model.Client;

public class ClientTest {
    @Test
    @DisplayName("Should create valid client")
    void shouldCreateValidClient() {
        Client client = Client.builder()
                .clientId(UUID.randomUUID())
                .name("John Doe")
                .phone("+593987654321")
                .birthDate(LocalDate.of(1990, 5, 15))
                .status(Client.ClientStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(client);
        assertEquals("John Doe", client.getName());
        assertTrue(client.isActive());
    }

    @Test
    @DisplayName("Should activate client successfully")
    void shouldActivateClient() {
        Client client = Client.builder()
                .status(Client.ClientStatus.INACTIVE)
                .build();

        client.activate();

        assertEquals(Client.ClientStatus.ACTIVE, client.getStatus());
        assertTrue(client.isActive());
    }

    @Test
    @DisplayName("Should not activate suspended client")
    void shouldNotActivateSuspendedClient() {

        Client client = Client.builder()
                .status(Client.ClientStatus.SUSPENDED)
                .build();

        assertThrows(DomainException.class, client::activate);
    }
}
