package com.app.payment_service.repository;

import com.app.payment_service.entity.TransactionPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionPayRepository extends JpaRepository<TransactionPay, String> {
}
