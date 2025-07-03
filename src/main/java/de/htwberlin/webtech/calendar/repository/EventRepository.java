package de.htwberlin.webtech.calendar.repository;

import de.htwberlin.webtech.calendar.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository // Kennzeichnet dieses Interface als Spring Data JPA Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    // Dieses Interface erbt bereits alle grundlegenden CRUD-Methoden (save, findById, findAll, deleteById, etc.)
    // von JpaRepository.

    // JpaSpecificationExecutor<Event> ermöglicht die Nutzung von Spring Data JPA Specifications,
    // um komplexe und dynamische Abfragen zu erstellen (wie z.B. für Filter).

    // Hier könnten bei Bedarf auch benutzerdefinierte Finder-Methoden definiert werden,
    // die JpaSpecificationExecutor nicht abdeckt oder für die eine einfache Methode ausreicht.
    // Beispiel:
    // List<Event> findByTitleContainingIgnoreCase(String titlePart);
    // List<Event> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
}