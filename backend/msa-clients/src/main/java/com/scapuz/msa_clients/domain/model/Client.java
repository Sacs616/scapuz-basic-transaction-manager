package com.scapuz.msa_clients.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.scapuz.msa_clients.domain.exception.DomainException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Client extends Person {

    private UUID clientId;
    private String passwordHash;
    private ClientStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ClientStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED
    }

    public void activate() {
        if (status == ClientStatus.SUSPENDED) {
            throw new DomainException("Can't activate suspended client");
        }
        this.status = ClientStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = ClientStatus.INACTIVE;
    }

    public void suspend() {
        this.status = ClientStatus.SUSPENDED;
    }

    public boolean isActive() {
        return this.status == ClientStatus.ACTIVE;
    }

}
