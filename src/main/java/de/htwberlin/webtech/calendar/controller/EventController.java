package de.htwberlin.webtech.calendar.controller;

import de.htwberlin.webtech.calendar.model.Event;
import de.htwberlin.webtech.calendar.service.EventService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = {
        "http://localhost:5173", // Erlaubt Anfragen vom lokalen Frontend-Entwicklungsserver
        "https://event-calendar-frontend.onrender.com" // Erlaubt Anfragen vom deployed Frontend-Produktionsserver
})
public class EventController {

    private final EventService eventService; // Abhängigkeit zum EventService

    // Konstruktor für Dependency Injection: Spring fügt automatisch eine Instanz von EventService ein
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Erstellt ein neues Event.
     * Erwartet ein Event-Objekt im Request Body.
     * @param event Das zu erstellende Event-Objekt.
     * @return Das erstellte Event mit generierter ID und Status 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventService.createEvent(event);
    }

    /**
     * Ruft alle Events ab, optional gefiltert nach Start-/Enddatum, Titel, Beschreibung oder Status.
     * Wenn keine Filterparameter übergeben werden, werden alle Events zurückgegeben.
     * @param startDate Filter: Events, die an oder nach diesem Datum beginnen.
     * @param endDate Filter: Events, die an oder vor diesem Datum enden.
     * @param title Filter: Events, deren Titel den angegebenen String enthält (fall-insensitiv).
     * @param description Filter: Events, deren Beschreibung den angegebenen String enthält (fall-insensitiv).
     * @param isCompleted Filter: Events nach ihrem Erledigungsstatus.
     * @return Eine Liste von Events, die den Filterkriterien entsprechen.
     */
    @GetMapping
    public List<Event> getEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Boolean isCompleted
    ) {
        // Prüft, ob irgendwelche Filterparameter vorhanden sind
        if (startDate == null && endDate == null && title == null && description == null && isCompleted == null) {
            // Wenn keine Filter vorhanden sind, gib alle Events zurück
            return eventService.getAllEvents();
        } else {
            // Andernfalls, wende die Filter über den Service an
            return eventService.getFilteredEvents(startDate, endDate, title, description, isCompleted);
        }
    }

    /**
     * Ruft ein einzelnes Event anhand seiner ID ab.
     * @param id Die ID des abzurufenden Events.
     * @return Das Event-Objekt mit Status 200 OK, oder 404 Not Found, wenn das Event nicht existiert.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Aktualisiert ein vorhandenes Event vollständig.
     * Erwartet die ID des Events in der URL und das vollständige Event-Objekt im Request Body.
     * @param id Die ID des zu aktualisierenden Events.
     * @param updatedEvent Das Event-Objekt mit den aktualisierten Daten.
     * @return Das aktualisierte Event mit Status 200 OK, oder 404 Not Found, wenn das Event nicht existiert.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event updatedEvent) {
        try {
            Event savedEvent = eventService.updateEvent(id, updatedEvent);
            return ResponseEntity.ok(savedEvent);
        } catch (ResponseStatusException e) {
            // Fängt Ausnahmen vom Service ab und gibt den entsprechenden HTTP-Status zurück (z.B. 404)
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    /**
     * Löscht ein Event anhand seiner ID.
     * @param id Die ID des zu löschenden Events.
     * @return Status 204 No Content bei Erfolg, oder 404 Not Found, wenn das Event nicht existiert.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            // Fängt Ausnahmen vom Service ab und gibt den entsprechenden HTTP-Status zurück (z.B. 404)
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    /**
     * Aktualisiert den Erledigungsstatus eines Events.
     * Verwendet PATCH, um nur einen Teil der Ressource zu ändern.
     * @param id Die ID des Events, dessen Status aktualisiert werden soll.
     * @param isCompleted Der neue Erledigungsstatus (true für erledigt, false für nicht erledigt).
     * @return Das aktualisierte Event mit Status 200 OK, oder 404 Not Found, wenn das Event nicht existiert.
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Event> markEventAsCompleted(@PathVariable Long id, @RequestParam Boolean isCompleted) {
        try {
            Event updatedEvent = eventService.updateEventCompletionStatus(id, isCompleted);
            return ResponseEntity.ok(updatedEvent);
        } catch (ResponseStatusException e) {
            // Fängt Ausnahmen vom Service ab und gibt den entsprechenden HTTP-Status zurück (z.B. 404)
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}