package com.laft.account.application.service;

import com.laft.account.application.dto.CreateAccountDTO;
import com.laft.account.application.dto.AccountResponseDTO;
import com.laft.account.application.mapper.AccountMapper;
import com.laft.common.constants.ErrorConstants;
import com.laft.common.exception.BusinessValidationException;
import com.laft.common.exception.ResourceNotFoundException;
import com.laft.shared.domain.model.Account;
import com.laft.shared.domain.model.Status;
import com.laft.shared.domain.model.AccountType;
import com.laft.shared.domain.repository.AccountRepository;
import com.laft.shared.domain.repository.StatusRepository;
import com.laft.shared.domain.repository.AccountTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de accounts
 * Patrón: Repository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final StatusRepository statusRepository;
    private final AccountMapper accountMapper;
    
    /**
     * Obtiene todas las accounts
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        log.debug("Obteniendo todas las accounts");
        return accountRepository.findAll().stream()
            .map(accountMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una cuenta por número
     */
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountByNumero(String accountNumber) {
        log.debug("Obteniendo cuenta: {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CUENTA_NO_ENCONTRADA));
        return accountMapper.toResponseDTO(account);
    }
    
    /**
     * Obtiene accounts por cliente
     */
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByClient(String clientId) {
        log.debug("Obteniendo accounts del cliente: {}", clientId);
        return accountRepository.findByClientId(clientId).stream()
            .map(accountMapper::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Crea una nueva cuenta
     */
    @Transactional
    public AccountResponseDTO createAccount(CreateAccountDTO dto) {
        log.debug("Creando nueva cuenta: {}", dto.getAccountNumber());
        
        // Validar que no exista una cuenta con el mismo número
        if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new BusinessValidationException(ErrorConstants.CUENTA_YA_EXISTE);
        }
        
        // Buscar tipo de cuenta
        AccountType accountType = accountTypeRepository.findByCodeAndActiveTrue(dto.getAccountType())
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorConstants.TIPO_CUENTA_NO_ENCONTRADO + ": " + dto.getAccountType()
            ));
        
        // Buscar estado
        Status status = statusRepository.findByCodeAndActiveTrue(dto.getStatus())
            .orElseThrow(() -> new ResourceNotFoundException(
                ErrorConstants.ESTADO_NO_ENCONTRADO + ": " + dto.getStatus()
            ));
        
        // Crear cuenta usando Builder
        Account account = Account.builder()
            .accountNumber(dto.getAccountNumber())
            .accountType(accountType)
            .initialBalance(dto.getInitialBalance())
            .currentBalance(dto.getInitialBalance())
            .status(status)
            .clientId(dto.getClientId())
            .build();
        
        // Guardar cuenta
        Account savedAccount = accountRepository.save(account);
        log.info("Account creada exitosamente: {}", savedAccount.getAccountNumber());
        
        return accountMapper.toResponseDTO(savedAccount);
    }
    
    /**
     * Actualiza una cuenta
     */
    @Transactional
    public AccountResponseDTO updateAccount(String accountNumber, CreateAccountDTO dto) {
        log.debug("Actualizando cuenta: {}", accountNumber);
        
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CUENTA_NO_ENCONTRADA));
        
        // Actualizar tipo de cuenta si cambió
        if (dto.getAccountType() != null) {
            AccountType accountType = accountTypeRepository.findByCodeAndActiveTrue(dto.getAccountType())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.TIPO_CUENTA_NO_ENCONTRADO));
            account.setAccountType(accountType);
        }
        
        // Actualizar estado si cambió
        if (dto.getStatus() != null) {
            Status status = statusRepository.findByCodeAndActiveTrue(dto.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.ESTADO_NO_ENCONTRADO));
            account.setStatus(status);
        }
        
        Account updatedAccount = accountRepository.save(account);
        log.info("Account actualizada exitosamente: {}", accountNumber);
        
        return accountMapper.toResponseDTO(updatedAccount);
    }
    
    /**
     * Elimina una cuenta (soft delete)
     */
    @Transactional
    public void deleteAccount(String accountNumber) {
        log.debug("Eliminando cuenta: {}", accountNumber);
        
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.CUENTA_NO_ENCONTRADA));
        
        // Soft delete: cambiar estado a INACTIVO
        Status statusInactivo = statusRepository.findByCode("INACTIVO")
            .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.ESTADO_NO_ENCONTRADO));
        
        account.setStatus(statusInactivo);
        accountRepository.save(account);
        
        log.info("Account eliminada (inactivada) exitosamente: {}", accountNumber);
    }
}
