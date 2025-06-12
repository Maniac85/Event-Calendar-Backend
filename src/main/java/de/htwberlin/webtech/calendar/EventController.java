package de.htwberlin.webtech.calendar;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = {
        "http://localhost:5173", // Lokaler Frontend-Entwicklungsserver
        "https://event-calendar-frontend.onrender.com"
})
public class EventController {

    @GetMapping("/events")
    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        Event event1 = new Event(
                1L,
                "Projektarbeit",
                "Letzten Stand abfragen",
                LocalDateTime.of(2025, 5, 21, 10, 0),
                LocalDateTime.of(2025, 5, 21, 11, 0)
        );
        events.add(event1);

        Event event2 = new Event(
                2L,
                "Abgabe",
                "Alles hochladen",
                LocalDateTime.of(2025, 7, 16, 12, 30),
                LocalDateTime.of(2025, 7, 16, 13, 30)
        );
        events.add(event2);

        return events;
    }
}
