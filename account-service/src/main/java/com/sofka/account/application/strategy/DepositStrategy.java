package com.laft.account.application.strategy;

import com.laft.shared.domain.model.Account;
import com.laft.shared.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Estrategia para procesar depósitos
 * Patrón: Strategy (implementación concreta)
 */
@Component
public class DepositStrategy implements TransactionStrategy {
    
    @Override
    public Transaction procesarTransaction(Account account, BigDecimal amount) {
        // Calcular nuevo saldo
        BigDecimal nuevoSaldo = account.getCurrentBalance().add(amount);
        
        // Actualizar saldo de la cuenta
        account.setCurrentBalance(nuevoSaldo);
        
        // Crear movimiento
        return Transaction.builder()
            .transactionType(Transaction.TransactionType.DEPOSITO)
            .amount(amount)
            .balance(nuevoSaldo)
            .account(account)
            .build();
    }
    
    @Override
    public boolean validarTransaction(Account account, BigDecimal amount) {
        // Los depósitos siempre son válidos si el valor es positivo
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
