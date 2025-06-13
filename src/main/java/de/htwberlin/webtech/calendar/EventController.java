package de.htwberlin.webtech.calendar;

import de.htwberlin.webtech.calendar.EventRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://localhost:5173", // Lokaler Frontend-Entwicklungsserver
        "https://event-calendar-frontend.onrender.com"
})
public class EventController {

    private final EventRepository eventRepository; // Füge das Repository als finales Feld hinzu

    // Konstruktor für Dependency Injection
    // Spring wird hier automatisch eine Instanz von EventRepository einfügen
    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public List<Event> getEvents() {
        // Ändere dies, um alle Events aus der Datenbank abzurufen
        return eventRepository.findAll();
    }

    // NEU: POST-Methode zum Erstellen eines neuen Events
    @PostMapping("/events")
    public Event createEvent(@RequestBody Event event) {
        // Speichere das empfangene Event-Objekt in der Datenbank
        return eventRepository.save(event);
    }
}
