package com.app.booking.internal.event_service.repository;

import com.app.booking.internal.event_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat,Integer> {
    List<Seat> findAllByEventId(Integer eventID);

    @Query(
            value = "SELECT e.price_ticket FROM seats s JOIN events e ON s.event_id = e.id WHERE s.id = :seatId",
            nativeQuery = true
    )
    Integer findPriceBySeatId(@Param("seatId") Integer seatId);
}
