package com.laft.account.infrastructure.controller;

import com.laft.account.application.dto.CreateTransactionDTO;
import com.laft.account.application.dto.TransactionResponseDTO;
import com.laft.account.application.service.TransactionService;
import com.laft.common.constants.ApiConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de transactions
 * Endpoints: /api/transactions
 */
@RestController
@RequestMapping(ApiConstants.MOVIMIENTOS_PATH)
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * GET /api/transactions
     * Obtiene todos los transactions
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        log.info("GET /transactions - Obteniendo todos los transactions");
        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * GET /api/transactions/{id}
     * Obtiene un movimiento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable Long id) {
        log.info("GET /transactions/{} - Obteniendo movimiento", id);
        TransactionResponseDTO transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }
    
    /**
     * GET /api/transactions/cuenta/{accountNumber}
     * Obtiene transactions por cuenta
     */
    @GetMapping("/cuenta/{accountNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccount(@PathVariable String accountNumber) {
        log.info("GET /transactions/cuenta/{} - Obteniendo transactions de la cuenta", accountNumber);
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccount(accountNumber);
        return ResponseEntity.ok(transactions);
    }
    
    /**
     * POST /api/transactions
     * Crea un nuevo movimiento
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody CreateTransactionDTO dto) {
        log.info("POST /transactions - Creando nuevo movimiento");
        TransactionResponseDTO transaction = transactionService.createTransaction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    
    /**
     * DELETE /api/transactions/{id}
     * Elimina un movimiento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("DELETE /transactions/{} - Eliminando movimiento", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
