package com.app.ticket_service.repository.client;

import com.app.ticket_common_library.config.security.FeignConfig;
import com.app.ticket_service.dto.Event;
import com.app.ticket_service.dto.Seat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "event-client", url = "${app.service.event}" + "/events", configuration = FeignConfig.class)
public interface EventClient {
    @GetMapping("/{id}")
    Seat findSeatById(@PathVariable Integer id);

    @GetMapping("/lock/{id}")
    Seat findSeatForUpdate(@PathVariable Integer id);//Lock

    @GetMapping("/{id}")
    Event findBySeatId(@PathVariable Integer id);

    @PostMapping
    void save(Seat seat);

    @GetMapping("/exists/{id}")
    boolean existsById(@PathVariable Integer id);
}
