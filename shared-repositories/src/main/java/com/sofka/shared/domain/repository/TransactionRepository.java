package com.laft.shared.domain.repository;

import com.laft.shared.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Transaction
 * Patrón: Repository
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByAccountId(Long cuentaId);
    
    List<Transaction> findByAccountIdOrderByDateDesc(Long cuentaId);
    
    @Query("SELECT m FROM Transaction m WHERE m.account.id = :cuentaId AND m.date BETWEEN :startDate AND :endDate ORDER BY m.date DESC")
    List<Transaction> findByAccountIdAndFechaBetween(
        @Param("cuentaId") Long cuentaId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT m FROM Transaction m WHERE m.account.clientId = :clientId AND m.date BETWEEN :startDate AND :endDate ORDER BY m.date DESC")
    List<Transaction> findByClientIdAndFechaBetween(
        @Param("clientId") String clientId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
