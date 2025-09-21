package com.app.booking.payment_service_test.controller;

import com.app.booking.internal.payment_service.controller.PaymentController;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.model_mock.EntityMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PaymentService paymentService;
    @MockitoBean
    VNPayService vnPayService;

    Payment payment;
    List<Payment> payments;

    @BeforeEach
    void initData(){
        payment = EntityMock.paymentMock();
        payments = new ArrayList<>();
        payments.add(payment);
        payments.add(payment);
        payments.add(payment);
    }


    @Test
    void getAll_success() throws Exception {
        when(paymentService.findAll(any(Pageable.class))).thenReturn(payments);

        mockMvc.perform(get("/payments/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void findAllByOrganizerId_success() throws Exception {
        String userID = "id";
        when(paymentService.findAllByOrganizerId(eq(userID),any(Pageable.class))).thenReturn(payments);

        mockMvc.perform(get("/payments/public/user/{userID}",userID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.length()").value(3));
    }

    @Test
    void update_success() throws Exception {
        Integer ticketId = 1;
        String response= "response";
        when(paymentService.retryPay(ticketId)).thenReturn(response);

        mockMvc.perform(post("/payments/public/retry/{ticketId}",ticketId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result").value(response));
    }

}
