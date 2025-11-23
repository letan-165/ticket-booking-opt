package com.app.ticket_service.controller.local;

import com.app.ticket_service.entity.Ticket;
import com.app.ticket_service.service.TicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketControllerLocal {
    TicketService ticketService;

    @PostMapping("/{ticketId}")
    public Ticket findById(@PathVariable Integer ticketId) {
        return ticketService.findById(ticketId);
    }

    @PutMapping("/status/{ticketId}/{paid}")
    public Boolean updateStatus(@PathVariable Integer ticketId, @PathVariable boolean paid) {
        ticketService.updateStatus(ticketId, paid);
        return true;
    }
}
