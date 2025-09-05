package com.app.booking.internal.payment_service.repository;

import com.app.booking.internal.payment_service.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Integer> {
    @Query(value = """
    SELECT p.* 
    FROM payments p
    JOIN tickets t ON p.ticket_id = t.id
    JOIN seats s ON t.seat_id = s.id
    JOIN events e ON s.event_id = e.id
    WHERE e.organizer_id = :organizerId
""", nativeQuery = true)
    Page<Payment> findAllByOrganizerId(@Param("organizerId") String organizerId, Pageable pageable);
}
