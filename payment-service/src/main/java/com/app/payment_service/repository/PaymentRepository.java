package com.app.payment_service.repository;

import com.app.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByTicketId(Integer ticketID);

    List<Payment> findAllByTicketId(List<Integer> ticketIDs);
}
