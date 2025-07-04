
package de.htwberlin.webtech.calendar.service;

import de.htwberlin.webtech.calendar.model.Event;
import de.htwberlin.webtech.calendar.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        event1 = new Event(1L, "Meeting", "Team Meeting",
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 10, 0),
                false);

        event2 = new Event(2L, "Project Deadline", "Final submission",
                LocalDateTime.of(2025, 7, 15, 17, 0),
                LocalDateTime.of(2025, 7, 15, 23, 59),
                true);
    }

    @Test
    @DisplayName("Should retrieve all events with no filters")
    void shouldGetAllEventsNoFilters() {
        // Hier wird Ihre getAllEvents() Methode im Service getestet, die findAll() aufruft
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        List<Event> events = eventService.getAllEvents();

        assertNotNull(events);
        assertEquals(2, events.size());
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));

        // Überprüfe, dass die findAll Methode auf dem Mock genau einmal aufgerufen wurde
        verify(eventRepository, times(1)).findAll();
        // Stellen Sie sicher, dass getFilteredEvents nicht aufgerufen wurde, da getAllEvents verwendet wird
        verify(eventRepository, never()).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should retrieve events filtered by title and completion status")
    void shouldGetFilteredEventsByTitleAndCompletion() {
        String titleFilter = "Meeting";
        Boolean isCompletedFilter = false;

        // Mocken des Verhaltens von findAll(Specification)
        // Wir können die genaue Spezifikation nicht leicht mocken, da sie dynamisch erstellt wird.
        // Stattdessen mocken wir, dass findAll mit *einer beliebigen Spezifikation* ein Ergebnis liefert.
        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(event1));

        List<Event> events = eventService.getFilteredEvents(
                null, null, titleFilter, null, isCompletedFilter);

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(event1.getTitle(), events.get(0).getTitle());
        assertFalse(events.get(0).getIsCompleted());

        // Überprüfen, dass findAll mit einer Spezifikation aufgerufen wurde
        verify(eventRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Should retrieve event by ID")
    void shouldGetEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));

        Optional<Event> foundEvent = eventService.getEventById(1L);

        assertTrue(foundEvent.isPresent());
        assertEquals(event1.getTitle(), foundEvent.get().getTitle());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty optional if event not found by ID")
    void shouldReturnEmptyOptionalWhenEventNotFoundById() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Event> foundEvent = eventService.getEventById(99L);

        assertFalse(foundEvent.isPresent());
        verify(eventRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should create a new event successfully")
    void shouldCreateEvent() {
        Event newEvent = new Event(null, "New Task", "Description",
                LocalDateTime.of(2025, 8, 1, 9, 0),
                LocalDateTime.of(2025, 8, 1, 10, 0),
                false);
        // Wenn save() aufgerufen wird, gib dasselbe Event mit einer ID zurück (simuliert DB-Verhalten)
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event savedEvent = invocation.getArgument(0);
            savedEvent.setId(3L); // Simulate ID generation
            return savedEvent;
        });

        Event createdEvent = eventService.createEvent(newEvent);

        assertNotNull(createdEvent);
        assertEquals(newEvent.getTitle(), createdEvent.getTitle());
        assertNotNull(createdEvent.getId()); // ID sollte gesetzt sein
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should update an existing event successfully")
    void shouldUpdateEvent() {
        Event updatedDetails = new Event(1L, "Updated Meeting", "Updated Description",
                LocalDateTime.of(2025, 7, 10, 9, 30),
                LocalDateTime.of(2025, 7, 10, 10, 30),
                true);

        when(eventRepository.existsById(1L)).thenReturn(true); // Simulieren, dass Event existiert
        when(eventRepository.save(any(Event.class))).thenReturn(updatedDetails); // Simulieren des Speicherns

        Event result = eventService.updateEvent(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Meeting", result.getTitle());
        assertTrue(result.getIsCompleted());
        verify(eventRepository, times(1)).existsById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException if event not found for update")
    void shouldThrowExceptionWhenUpdatingNonExistentEvent() {
        Event updatedDetails = new Event(99L, "Non Existent", "Details",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), false);
        when(eventRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            eventService.updateEvent(99L, updatedDetails);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Event not found with ID: 99", exception.getReason());
        verify(eventRepository, times(1)).existsById(99L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Should delete an existing event")
    void shouldDeleteEvent() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(1L);

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).existsById(1L);
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResponseStatusException if event not found for delete")
    void shouldThrowExceptionWhenDeletingNonExistentEvent() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            eventService.deleteEvent(99L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Event not found with ID: 99", exception.getReason());
        verify(eventRepository, times(1)).existsById(99L);
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should update completion status of an event")
    void shouldUpdateEventCompletionStatus() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event savedEvent = invocation.getArgument(0);
            return savedEvent;
        });

        Event updatedEvent = eventService.updateEventCompletionStatus(1L, true);

        assertNotNull(updatedEvent);
        assertEquals(1L, updatedEvent.getId());
        assertTrue(updatedEvent.getIsCompleted()); // Verwenden Sie getIsCompleted() wegen Lombok
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should throw ResponseStatusException if event not found for completion status update")
    void shouldThrowExceptionWhenUpdatingCompletionStatusForNonExistentEvent() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            eventService.updateEventCompletionStatus(99L, true);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Event not found with ID: 99", exception.getReason());
        verify(eventRepository, times(1)).findById(99L);
        verify(eventRepository, never()).save(any(Event.class));
    }
}