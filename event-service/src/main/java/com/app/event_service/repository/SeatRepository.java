package com.app.event_service.repository;

import com.app.event_service.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findSeatForUpdate(@Param("id") Integer seatId);

    List<Seat> findAllByEventId(Integer eventID);

    @Query(
            value = "SELECT e.price_ticket FROM seats s JOIN events e ON s.event_id = e.id WHERE s.id = :seatId",
            nativeQuery = true
    )
    Integer findPriceBySeatId(@Param("seatId") Integer seatId);
}
