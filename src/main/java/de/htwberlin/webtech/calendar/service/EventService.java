package de.htwberlin.webtech.calendar.service;

import de.htwberlin.webtech.calendar.model.Event;
import de.htwberlin.webtech.calendar.repository.EventRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Markiert diese Klasse als Spring Service, der Geschäftslogik enthält
public class EventService {

    private final EventRepository eventRepository;

    // Konstruktor für Dependency Injection: Spring injiziert automatisch eine Instanz von EventRepository
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Erstellt ein neues Event in der Datenbank.
     * @param event Das zu speichernde Event-Objekt.
     * @return Das gespeicherte Event mit der zugewiesenen ID.
     */
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Ruft alle vorhandenen Events aus der Datenbank ab.
     * @return Eine Liste aller Events.
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Ruft ein einzelnes Event anhand seiner ID ab.
     * @param id Die ID des abzurufenden Events.
     * @return Ein Optional, das das Event enthält, falls es gefunden wurde.
     */
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    /**
     * Aktualisiert ein vorhandenes Event.
     * Wirft eine ResponseStatusException (HTTP 404), wenn das Event nicht gefunden wird.
     * @param id Die ID des zu aktualisierenden Events.
     * @param updatedEvent Das Event-Objekt mit den aktualisierten Daten.
     * @return Das aktualisierte Event.
     */
    @Transactional // Stellt sicher, dass die Operation in einer Transaktion ausgeführt wird
    public Event updateEvent(Long id, Event updatedEvent) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id);
        }
        updatedEvent.setId(id); // Sicherstellen, dass die ID korrekt gesetzt ist
        return eventRepository.save(updatedEvent);
    }

    /**
     * Löscht ein Event anhand seiner ID.
     * Wirft eine ResponseStatusException (HTTP 404), wenn das Event nicht gefunden wird.
     * @param id Die ID des zu löschenden Events.
     */
    @Transactional // Stellt sicher, dass die Operation in einer Transaktion ausgeführt wird
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id);
        }
        eventRepository.deleteById(id);
    }

    /**
     * Ruft Events basierend auf optionalen Filterkriterien ab.
     * Die Filter können kombiniert werden.
     * @param startDate Events, die an oder nach diesem Datum beginnen.
     * @param endDate Events, die an oder vor diesem Datum enden.
     * @param title Events, deren Titel den angegebenen String enthält (fall-insensitiv).
     * @param description Events, deren Beschreibung den angegebenen String enthält (fall-insensitiv).
     * @param isCompleted Events nach ihrem Erledigungsstatus.
     * @return Eine Liste von Events, die den Filterkriterien entsprechen.
     */
    public List<Event> getFilteredEvents(
            LocalDate startDate,
            LocalDate endDate,
            String title,
            String description,
            Boolean isCompleted
    ) {
        return eventRepository.findAll((Specification<Event>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                // Filtert Events, die am oder nach dem angegebenen Startdatum beginnen
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDateTime"), startDate.atStartOfDay()));
            }
            if (endDate != null) {
                // Filtert Events, die am oder vor dem angegebenen Enddatum enden (bis Ende des Tages)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDateTime"), endDate.atTime(LocalTime.MAX)));
            }
            if (title != null && !title.isEmpty()) {
                // Filtert Events, deren Titel den String enthält (fall-insensitiv)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (description != null && !description.isEmpty()) {
                // Filtert Events, deren Beschreibung den String enthält (fall-insensitiv)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }
            if (isCompleted != null) {
                // Filtert Events basierend auf ihrem Erledigungsstatus
                predicates.add(criteriaBuilder.equal(root.get("isCompleted"), isCompleted));
            }

            // Kombiniert alle angewendeten Filter mit einem logischen UND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * Aktualisiert den Erledigungsstatus eines Events.
     * Wirft eine ResponseStatusException (HTTP 404), wenn das Event nicht gefunden wird.
     * @param id Die ID des Events, dessen Status aktualisiert werden soll.
     * @param isCompleted Der neue Erledigungsstatus (true für erledigt, false für nicht erledigt).
     * @return Das aktualisierte Event.
     */
    @Transactional // Stellt sicher, dass die Operation in einer Transaktion ausgeführt wird
    public Event updateEventCompletionStatus(Long id, Boolean isCompleted) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id));

        event.setIsCompleted(isCompleted);
        return eventRepository.save(event);
    }
}