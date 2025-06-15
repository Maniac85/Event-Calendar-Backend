package de.htwberlin.webtech.calendar.controller;

import de.htwberlin.webtech.calendar.model.Event;
import de.htwberlin.webtech.calendar.repository.EventRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = {
        "http://localhost:5173", // Erlaubt Anfragen vom lokalen Frontend-Entwicklungsserver
        "https://event-calendar-frontend.onrender.com" // Erlaubt Anfragen vom deployed Frontend-Produktionsserver
})
public class EventController {

    private final EventRepository eventRepository;

    // Konstruktor für Dependency Injection: Spring fügt automatisch eine Instanz von EventRepository ein
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // POST-Methode: Erstellt ein neues Event
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // HTTP 201 Created Status
    public Event createEvent(@Valid @RequestBody Event event) {
        // Speichert das neue Event in der Datenbank und gibt es zurück (mit generierter ID)
        return eventRepository.save(event);
    }

    // GET-Methode: Ruft alle vorhandenen Events ab
    @GetMapping
    public List<Event> getEvents() {
        // Sucht und gibt alle Event-Entitäten aus der Datenbank zurück
        return eventRepository.findAll();
    }

    // GET-Methode: Ruft ein einzelnes Event anhand seiner ID ab
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        // Sucht ein Event anhand der ID. Wenn gefunden, gibt es 200 OK zurück, sonst 404 Not Found.
        Optional<Event> event = eventRepository.findById(id);
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // PUT-Methode: Aktualisiert ein vorhandenes Event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event updatedEvent) {
        // Prüft, ob das Event existiert. Wenn nicht, 404 Not Found.
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Setzt die ID des Updates auf die ID aus der URL und speichert die Änderungen
        updatedEvent.setId(id);
        Event savedEvent = eventRepository.save(updatedEvent);
        return ResponseEntity.ok(savedEvent); // 200 OK mit dem aktualisierten Event
    }

    // DELETE-Methode: Löscht ein Event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        // Prüft, ob das Event existiert. Wenn nicht, 404 Not Found.
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Löscht das Event aus der Datenbank
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content (erfolgreich, aber keine Daten zurück)
    }
}
