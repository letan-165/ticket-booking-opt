package com.app.booking.internal.payment_service.entity;

import com.app.booking.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "ticket_id", nullable = false)
    Integer ticketId;
    Integer amount;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "transaction_id")
    String transactionId;

    @Column(name = "response_code")
    String responseCode;

    @Column(name = "bank_code")
    String bankCode;

    @Column(name = "pay_date")
    LocalDateTime payDate;
}
