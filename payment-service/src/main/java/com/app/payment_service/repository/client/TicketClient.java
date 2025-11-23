package com.app.payment_service.repository.client;

import com.app.payment_service.dto.Ticket;
import com.app.ticket_common_library.common.response.ApiResponse;
import com.app.ticket_common_library.config.security.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "ticket-client", url = "${app.service.ticket}" + "/tickets", configuration = FeignConfig.class)
public interface TicketClient {
    @GetMapping("/{ticketId}")
    Ticket findById(@PathVariable Integer ticketId);

    @PutMapping("/status/{ticketId}/{paid}")
    void updateStatus(@PathVariable Integer ticketId, @PathVariable Boolean paid);

    @GetMapping("/organizer/{organizerId}")
    ApiResponse<List<Ticket>> findAllByOrganizerId(@PathVariable String organizerId);
}
