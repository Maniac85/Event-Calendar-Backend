package de.htwberlin.webtech.calendar;
import de.htwberlin.webtech.calendar.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Optional, aber gute Praxis, um das Repository zu kennzeichnen
public interface EventRepository extends JpaRepository<Event, Long> {
    // Dieses Interface erbt bereits alle grundlegenden CRUD-Methoden
    // (save, findById, findAll, deleteById, etc.) von JpaRepository.

    // Hier könntest du später benutzerdefinierte Suchmethoden hinzufügen,
    // z.B. List<Event> findByTitle(String title);
}

