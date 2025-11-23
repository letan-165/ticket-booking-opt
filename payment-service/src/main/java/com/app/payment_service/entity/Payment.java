package com.app.payment_service.entity;

import com.app.ticket_common_library.common.enums.PaymentStatus;
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
}
