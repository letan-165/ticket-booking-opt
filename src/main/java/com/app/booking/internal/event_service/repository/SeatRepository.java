package com.app.booking.internal.event_service.repository;

import com.app.booking.internal.event_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findAllByEventId(Long eventID);
}
