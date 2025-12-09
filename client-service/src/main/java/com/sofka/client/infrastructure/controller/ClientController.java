package com.laft.client.infrastructure.controller;

import com.laft.client.application.dto.ClientResponseDTO;
import com.laft.client.application.dto.CreateClientDTO;
import com.laft.client.application.dto.UpdateClientDTO;
import com.laft.client.application.service.ClientService;
import com.laft.common.constants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de clients
 * Endpoints: /api/clients
 */
@RestController
@RequestMapping(ApiConstants.CLIENTES_PATH)
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    
    private final ClientService clienteService;
    
    /**
     * GET /api/clients
     * Obtiene todos los clients
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        log.info("GET /clients - Obteniendo todos los clients");
        List<ClientResponseDTO> clients = clienteService.getAllClients();
        return ResponseEntity.ok(clients);
    }
    
    /**
     * GET /api/clients/{clientId}
     * Obtiene un cliente por ID
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable String clientId) {
        log.info("GET /clients/{} - Obteniendo cliente", clientId);
        ClientResponseDTO cliente = clienteService.getClientById(clientId);
        return ResponseEntity.ok(cliente);
    }
    
    /**
     * GET /api/clients/identificacion/{identificacion}
     * Obtiene un cliente por identificación
     */
    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<ClientResponseDTO> getClientByIdentificacion(@PathVariable String identificacion) {
        log.info("GET /clients/identificacion/{} - Obteniendo cliente", identificacion);
        ClientResponseDTO cliente = clienteService.getClientByIdentificacion(identificacion);
        return ResponseEntity.ok(cliente);
    }
    
    /**
     * POST /api/clients
     * Crea un nuevo cliente
     */
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody CreateClientDTO dto) {
        log.info("POST /clients - Creando nuevo cliente");
        ClientResponseDTO cliente = clienteService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }
    
    /**
     * PUT /api/clients/{clientId}
     * Actualiza un cliente existente
     */
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable String clientId,
            @Valid @RequestBody UpdateClientDTO dto) {
        log.info("PUT /clients/{} - Actualizando cliente", clientId);
        ClientResponseDTO cliente = clienteService.updateClient(clientId, dto);
        return ResponseEntity.ok(cliente);
    }
    
    /**
     * PATCH /api/clients/{clientId}
     * Actualización parcial de un cliente
     */
    @PatchMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> patchClient(
            @PathVariable String clientId,
            @RequestBody UpdateClientDTO dto) {
        log.info("PATCH /clients/{} - Actualización parcial de cliente", clientId);
        ClientResponseDTO cliente = clienteService.updateClient(clientId, dto);
        return ResponseEntity.ok(cliente);
    }
    
    /**
     * DELETE /api/clients/{clientId}
     * Elimina un cliente (soft delete)
     */
    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId) {
        log.info("DELETE /clients/{} - Eliminando cliente", clientId);
        clienteService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }
}
