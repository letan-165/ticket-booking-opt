package com.app.booking.internal.payment_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionPay {
    @Id
    String id;

    @Column(name = "txn_ref")
    Integer txnRef;

    @Column(name = "gateway_type")
    String gatewayType;

    @Column(name = "amount")
    Integer amount;

    @Column(name = "extra_info")
    String extraInfo;

    @Column(name = "response_code")
    String responseCode;

    @Column(name = "bank_code")
    String bankCode;

    @Column(name = "pay_date")
    LocalDateTime payDate;
}
