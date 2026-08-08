package com.scapuz.msa_accounts.infrastructure.adapter.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.scapuz.msa_accounts.domain.exception.ClientServiceException;
import com.scapuz.msa_accounts.infrastructure.adapter.client.dto.ClientDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.client.url:http://client-service:8080}")
    private String clientServiceUrl;

    @CircuitBreaker(name = "clientService", fallbackMethod = "getClientFallback")
    @Retry(name = "clientService")
    @TimeLimiter(name = "clientService")
    public CompletableFuture<ClientDTO> getClient(UUID clientId) {
        log.debug("Fetching client {} from client-service", clientId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = clientServiceUrl + "/api/v1/clients/" + clientId;
                log.info("REQUESTING TO: {}", url);

                ResponseEntity<ClientDTO> response = restTemplate.getForEntity(
                        url,
                        ClientDTO.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    ClientDTO client = response.getBody();
                    log.debug("Successfully fetched client: {}", client.getClientId());
                    return client;
                }

                throw new ClientServiceException("Client service returned empty response");

            } catch (HttpClientErrorException.NotFound e) {
                log.error("Client not found: {}", clientId);
                throw new ClientServiceException("Client not found: " + clientId);

            } catch (HttpClientErrorException e) {
                log.error("Client error from client-service: {} - {}", e.getStatusCode(), e.getMessage());
                throw new ClientServiceException("Invalid client request: " + e.getMessage());

            } catch (HttpServerErrorException e) {
                log.error("Server error from client-service: {} - {}", e.getStatusCode(), e.getMessage());
                throw new ClientServiceException("Client service unavailable: " + e.getMessage());

            } catch (ResourceAccessException e) {
                log.error("Cannot connect to client-service: {}", e.getMessage());
                throw new ClientServiceException("Client service unreachable: " + e.getMessage(), e);

            } catch (Exception e) {
                log.error("Unexpected error calling client-service: {}", e.getMessage(), e);
                throw new ClientServiceException("Error communicating with client service", e);
            }
        });
    }

    private CompletableFuture<ClientDTO> getClientFallback(UUID clientId, Throwable throwable) {
        log.warn("Circuit breaker OPEN for client-service. Using fallback for client: {}", clientId);
        log.warn("Reason: {}", throwable.getMessage());
        return CompletableFuture.failedFuture(
                new ClientServiceException(
                        "Client service is currently unavailable. Please try again later. " +
                                "Original error: " + throwable.getMessage()));
    }

    @CircuitBreaker(name = "clientService", fallbackMethod = "validateClientActiveFallback")
    @Retry(name = "clientService")
    public boolean isClientActive(UUID clientId) {
        log.debug("Validating if client {} is active", clientId);

        try {
            CompletableFuture<ClientDTO> futureClient = getClient(clientId);
            ClientDTO client = futureClient.join(); // Block and wait

            boolean isActive = "ACTIVE".equals(client.getStatus());
            log.debug("Client {} active status: {}", clientId, isActive);

            return isActive;

        } catch (Exception e) {
            log.error("Error validating client status: {}", e.getMessage());
            return false;
        }
    }

    private boolean validateClientActiveFallback(UUID clientId, Throwable throwable) {
        log.warn("Using fallback for client validation: {}", clientId);
        return false;
    }
}