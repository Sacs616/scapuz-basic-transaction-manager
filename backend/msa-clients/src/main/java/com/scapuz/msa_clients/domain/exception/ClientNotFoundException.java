package com.scapuz.msa_clients.domain.exception;

import java.util.UUID;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String identification) {
        super("Client not found identification: " + identification);
    }

    public ClientNotFoundException(UUID clientId) {
        super("Client not found clientId: " + clientId);
    }
}
