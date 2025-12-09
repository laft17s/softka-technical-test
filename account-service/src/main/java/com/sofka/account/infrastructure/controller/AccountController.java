package com.laft.account.infrastructure.controller;

import com.laft.account.application.dto.CreateAccountDTO;
import com.laft.account.application.dto.AccountResponseDTO;
import com.laft.account.application.service.AccountService;
import com.laft.common.constants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de accounts
 * Endpoints: /api/accounts
 */
@RestController
@RequestMapping(ApiConstants.CUENTAS_PATH)
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    
    private final AccountService accountService;
    
    /**
     * GET /api/accounts
     * Obtiene todas las accounts
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        log.info("GET /accounts - Obteniendo todas las accounts");
        List<AccountResponseDTO> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * GET /api/accounts/{accountNumber}
     * Obtiene una cuenta por número
     */
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByNumero(@PathVariable String accountNumber) {
        log.info("GET /accounts/{} - Obteniendo cuenta", accountNumber);
        AccountResponseDTO account = accountService.getAccountByNumero(accountNumber);
        return ResponseEntity.ok(account);
    }
    
    /**
     * GET /api/accounts/cliente/{clientId}
     * Obtiene accounts por cliente
     */
    @GetMapping("/cliente/{clientId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByClient(@PathVariable String clientId) {
        log.info("GET /accounts/cliente/{} - Obteniendo accounts del cliente", clientId);
        List<AccountResponseDTO> accounts = accountService.getAccountsByClient(clientId);
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * POST /api/accounts
     * Crea una nueva cuenta
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody CreateAccountDTO dto) {
        log.info("POST /accounts - Creando nueva cuenta");
        AccountResponseDTO account = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    /**
     * PUT /api/accounts/{accountNumber}
     * Actualiza una cuenta
     */
    @PutMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable String accountNumber,
            @Valid @RequestBody CreateAccountDTO dto) {
        log.info("PUT /accounts/{} - Actualizando cuenta", accountNumber);
        AccountResponseDTO account = accountService.updateAccount(accountNumber, dto);
        return ResponseEntity.ok(account);
    }
    
    /**
     * DELETE /api/accounts/{accountNumber}
     * Elimina una cuenta (soft delete)
     */
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        log.info("DELETE /accounts/{} - Eliminando cuenta", accountNumber);
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
