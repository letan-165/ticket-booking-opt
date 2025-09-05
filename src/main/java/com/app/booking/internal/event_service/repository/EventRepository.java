package com.app.booking.internal.event_service.repository;

import com.app.booking.internal.event_service.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    Page<Event> findAllByOrganizerId(String id, Pageable pageable);
}
