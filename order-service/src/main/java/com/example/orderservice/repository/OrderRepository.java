package com.example.orderservice.repository;

import com.example.orderservice.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUniqueOrderNumber(String uniqueOrderNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.uniqueOrderNumber = :uniqueOrderNumber")
    Optional<Order> findByUniqueOrderNumberForUpdate(@Param("uniqueOrderNumber") String uniqueOrderNumber);
}
