package com.scapuz.msa_clients.infrastructure.adapter.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scapuz.msa_clients.application.port.input.ClientService;
import com.scapuz.msa_clients.domain.model.Client;
import com.scapuz.msa_clients.infrastructure.adapter.rest.dto.ClientRequest;
import com.scapuz.msa_clients.infrastructure.adapter.rest.dto.ClientResponse;
import com.scapuz.msa_clients.infrastructure.adapter.rest.mapper.ClientDtoMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client CRUD", description = "APIs for client CRUD")
public class ClientController {

    private final ClientService clientService;
    private final ClientDtoMapper dtoMapper;

    @PostMapping()
    @Operation(summary = "Create a new client", description = "Creates a new client")
    @ApiResponse(responseCode = "201", description = "Client created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        log.info("REST request to create client with id: {}", request.getIdentification());

        Client client = dtoMapper.toDomain(request);
        Client createdClient = clientService.createClient(client);
        ClientResponse response = dtoMapper.toResponse(createdClient);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClientByClientId(
            @Parameter(description = "Client id") @PathVariable UUID clientId) {
        Client client = clientService.getClientById(clientId);
        ClientResponse response = dtoMapper.toResponse(client);
        return ResponseEntity.ok(response);
    }

    @GetMapping("identification/{identification}")
    @Operation(summary = "Get client by identification", description = "Retrieves a client by its identification")
    public ResponseEntity<ClientResponse> getClientByIdentificatioEntity(
            @Parameter(description = "Client identification") @PathVariable String identification) {
        Client client = clientService.getClientByIdentification(identification);
        ClientResponse response = dtoMapper.toResponse(client);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all clients", description = "Retrieves all clients")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        List<ClientResponse> responses = clients.stream()
                .map(dtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{clientId}")
    @Operation(summary = "Update client", description = "Updates an existing client")
    @ApiResponse(responseCode = "200", description = "Client updated successfully")
    @ApiResponse(responseCode = "404", description = "Client not found")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable UUID clientId,
            @Valid @RequestBody ClientRequest request) {
        log.info("REST request to update a client with id: {}", clientId);
        Client client = dtoMapper.toDomain(request);
        ClientResponse response = dtoMapper.toResponse(clientService.updateClient(clientId, client));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clientId}")
    @Operation(summary = "Delete client", description = "Deletes a client by their ID")
    @ApiResponse(responseCode = "204", description = "Client deleted successfully")
    @ApiResponse(responseCode = "404", description = "Client not found")
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "Client ID") @PathVariable UUID clientId) {

        log.info("REST request to delete client with id: {}", clientId);

        clientService.deleteClient(clientId);

        return ResponseEntity.noContent().build();
    }

}
