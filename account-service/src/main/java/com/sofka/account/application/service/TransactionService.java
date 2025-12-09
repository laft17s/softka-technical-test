package com.laft.account.application.service;

import com.laft.account.application.dto.CreateTransactionDTO;
import com.laft.account.application.dto.TransactionResponseDTO;
import com.laft.account.application.factory.TransactionStrategyFactory;
import com.laft.account.application.mapper.TransactionMapper;
import com.laft.account.application.strategy.TransactionStrategy;
import com.laft.common.constants.ErrorConstants;
import com.laft.common.exception.BusinessValidationException;
import com.laft.common.exception.ResourceNotFoundException;
import com.laft.shared.domain.model.Account;
import com.laft.shared.domain.model.Transaction;
import com.laft.shared.domain.repository.AccountRepository;
import com.laft.shared.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de transactions
 * Implementa patrones: Strategy, Factory, Repository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionStrategyFactory strategyFactory;
    private final TransactionMapper transactionMapper;
    
    /**
     * Obtiene todos los transactions
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        log.debug("Obteniendo todos los transactions");
        return transactionRepository.findAll().stream()
            .map(transactionMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene un movimiento por ID
     */
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(Long id) {
        log.debug("Obteniendo movimiento con ID: {}", id);
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.MOVIMIENTO_NO_ENCONTRADO));
        return transactionMapper.toResponseDTO(transaction);
    }
    
    /**
     * Crea un nuevo movimiento
     * Funcionalidades F2 y F3: Registro de transactions con validación de saldo
     */
    @Transactional
    public TransactionResponseDTO createTransaction(CreateTransactionDTO dto) {
        log.debug("Creando nuevo movimiento para cuenta: {}", dto.getAccountNumber());
        
        // Buscar cuenta
        Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CUENTA_NO_ENCONTRADA));
        
        // Validar que la cuenta esté activa
        if (!"ACTIVO".equals(account.getStatus().getCode())) {
            throw new BusinessValidationException(ErrorConstants.CUENTA_INACTIVA);
        }
        
        // Validar valor del movimiento
        if (dto.getAmount() == null || dto.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException(ErrorConstants.VALOR_MOVIMIENTO_INVALIDO);
        }
        
        // Obtener estrategia apropiada usando Factory pattern
        TransactionStrategy strategy = strategyFactory.getStrategy(dto.getTransactionType());
        
        // Procesar movimiento usando Strategy pattern
        // Esto lanzará SaldoNoDisponibleException si no hay saldo (F3)
        Transaction transaction = strategy.procesarTransaction(account, dto.getAmount());
        
        // Guardar cuenta actualizada y movimiento
        accountRepository.save(account);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Transaction creado exitosamente. ID: {}, Tipo: {}, Valor: {}, Nuevo saldo: {}", 
            savedTransaction.getId(), 
            savedTransaction.getTransactionType(),
            savedTransaction.getAmount(),
            savedTransaction.getBalance());
        
        return transactionMapper.toResponseDTO(savedTransaction);
    }
    
    /**
     * Obtiene transactions por cuenta
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccount(String accountNumber) {
        log.debug("Obteniendo transactions para cuenta: {}", accountNumber);
        
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CUENTA_NO_ENCONTRADA));
        
        return transactionRepository.findByAccountIdOrderByDateDesc(account.getId()).stream()
            .map(transactionMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Elimina un movimiento
     * Nota: En un sistema real, probablemente no se permitiría eliminar transactions
     */
    @Transactional
    public void deleteTransaction(Long id) {
        log.debug("Eliminando movimiento con ID: {}", id);
        
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.MOVIMIENTO_NO_ENCONTRADO));
        
        transactionRepository.delete(transaction);
        log.info("Transaction eliminado exitosamente con ID: {}", id);
    }
}
