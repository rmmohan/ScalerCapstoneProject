package com.rv.microservices.order.repository;

import com.rv.microservices.order.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Long> {
    PaymentStatus findByStatus(String status);
}