package com.scapuz.msa_accounts.domain.exception;

public class ClientServiceException extends DomainException {
    public ClientServiceException(String message) {
        super(message);
    }

    public ClientServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
