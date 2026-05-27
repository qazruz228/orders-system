package com.example.paymentservice.repository;

import com.example.paymentservice.entity.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByUniqueOrderNumber(String uniqueOrderNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT transaction FROM Transaction transaction WHERE transaction.uniqueOrderNumber = :uniqueOrderNumber")
    Optional<Transaction> findByUniqueOrderNumberForUpdate(@Param("uniqueOrderNumber") String uniqueOrderNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT transaction FROM Transaction transaction WHERE transaction.orderId = :orderId")
    Optional<Transaction> findByOrderIdForUpdate(@Param("orderId") Long orderId);
}
