package com.laft.account.application.strategy;

import com.laft.common.constants.ErrorConstants;
import com.laft.common.exception.SaldoNoDisponibleException;
import com.laft.shared.domain.model.Account;
import com.laft.shared.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia para procesar retiros
 * Patrón: Strategy (implementación concreta)
 */
@Component
public class WithdrawalStrategy implements TransactionStrategy {
    
    @Override
    public Transaction procesarTransaction(Account account, BigDecimal amount) {
        // Validar que hay saldo suficiente
        if (!validarTransaction(account, amount)) {
            throw new SaldoNoDisponibleException(ErrorConstants.SALDO_NO_DISPONIBLE);
        }
        
        // Calcular nuevo saldo (restar el amount)
        BigDecimal nuevoSaldo = account.getCurrentBalance().subtract(amount);
        
        // Actualizar saldo de la cuenta
        account.setCurrentBalance(nuevoSaldo);
        
        // Crear movimiento con valor negativo
        return Transaction.builder()
            .transactionType(Transaction.TransactionType.RETIRO)
            .amount(amount.negate()) // Valor negativo para retiros
            .balance(nuevoSaldo)
            .account(account)
            .build();
    }
    
    @Override
    public boolean validarTransaction(Account account, BigDecimal amount) {
        // Validar que el valor sea positivo y que haya saldo suficiente
        return amount.compareTo(BigDecimal.ZERO) > 0 && 
               account.getCurrentBalance().compareTo(amount) >= 0;
    }
}
