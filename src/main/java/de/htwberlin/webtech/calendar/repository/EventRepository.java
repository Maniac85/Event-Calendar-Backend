package de.htwberlin.webtech.calendar.repository;
import de.htwberlin.webtech.calendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Optional, aber gute Praxis, um das Repository zu kennzeichnen
public interface EventRepository extends JpaRepository<Event, Long> {
    // Dieses Interface erbt bereits alle grundlegenden CRUD-Methoden
    // (save, findById, findAll, deleteById, etc.) von JpaRepository.

    // Später könnten wir hier benutzerdefinierte Suchmethoden hinzufügen:

    // 1. Filtern nach (Teil-)Titel (case-insensitive):
    // List<Event> findByTitleContainingIgnoreCase(String titlePart);

    // Beispielaufruf:
    // List<Event> events = eventRepository.findByTitleContainingIgnoreCase("Geburtstag");

    // 2. Filtern nach Events in einem bestimmten Zeitraum:
    // List<Event> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Beispielaufruf:
    // LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
    // LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
    // List<Event> events = eventRepository.findByStartDateTimeBetween(start, end);

    // 3. Alle Events sortiert nach Startzeit (aufsteigend):
    // List<Event> findAllByOrderByStartDateTimeAsc();

    // Beispielaufruf:
    // List<Event> events = eventRepository.findAllByOrderByStartDateTimeAsc();
}

