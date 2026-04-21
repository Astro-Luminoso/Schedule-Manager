package dev.nbcsparta.assignment.schedulemanager.repository;

import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByAuthorId(Long authorId);
}
