// src/test/java/de/htwberlin/webtech/calendar/controller/EventControllerTest.java
package de.htwberlin.webtech.calendar.controller; // Passen Sie Ihr Paket an

import de.htwberlin.webtech.calendar.model.Event; // Passen Sie Ihr Paket an
import de.htwberlin.webtech.calendar.service.EventService; // Passen Sie Ihr Paket an
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Spezialisiert für Controller-Tests
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Zum Mocken von Services
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper; // Für JSON-Konvertierung
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Für LocalDateTime

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest ist leichter als @SpringBootTest, da es nur die Web-Schicht lädt
// Geben Sie hier den Controller an, den Sie testen möchten
@WebMvcTest(EventController.class)
@DisplayName("EventController Integration Tests")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc; // Ermöglicht das Senden von HTTP-Anfragen

    @MockitoBean // Erstellt einen Mock für den EventService im Spring Application Context
    private EventService eventService;

    // ObjectMapper zum Konvertieren von Java-Objekten in JSON und umgekehrt
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("GET /events should return all events")
    void shouldGetAllEvents() throws Exception {
        Event event1 = new Event(1L, "Meeting", "Team Meeting",
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 10, 0),
                false);
        Event event2 = new Event(2L, "Project Deadline", "Final submission",
                LocalDateTime.of(2025, 7, 15, 17, 0),
                LocalDateTime.of(2025, 7, 15, 23, 59),
                true);

        when(eventService.getAllEvents()).thenReturn(Arrays.asList(event1, event2));

        mockMvc.perform(MockMvcRequestBuilders.get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Meeting"))
                .andExpect(jsonPath("$[1].title").value("Project Deadline"));

        verify(eventService, times(1)).getAllEvents();
        verify(eventService, never()).getFilteredEvents(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("GET /events with filters should return filtered events")
    void shouldGetFilteredEvents() throws Exception {
        LocalDate startDate = LocalDate.of(2025, 7, 10);
        String title = "Meeting";
        Boolean isCompleted = false;

        Event event1 = new Event(1L, "Meeting", "Team Meeting",
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 10, 0),
                false);

        // Definieren des erwarteten Verhaltens des Service bei Filtern
        when(eventService.getFilteredEvents(
                eq(startDate), eq(null), eq(title), eq(null), eq(isCompleted)))
                .thenReturn(List.of(event1));

        mockMvc.perform(MockMvcRequestBuilders.get("/events")
                        .param("startDate", "2025-07-10")
                        .param("title", "Meeting")
                        .param("isCompleted", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Meeting"));

        verify(eventService, times(1)).getFilteredEvents(
                eq(startDate), eq(null), eq(title), eq(null), eq(isCompleted));
        verify(eventService, never()).getAllEvents(); // Sicherstellen, dass getAllEvents nicht aufgerufen wurde
    }


    @Test
    @DisplayName("GET /events/{id} should return event by ID")
    void shouldGetEventById() throws Exception {
        Event event = new Event(1L, "Meeting", "Team Meeting",
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 10, 0),
                false);

        when(eventService.getEventById(1L)).thenReturn(Optional.of(event));

        mockMvc.perform(MockMvcRequestBuilders.get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Meeting"));

        verify(eventService, times(1)).getEventById(1L);
    }

    @Test
    @DisplayName("GET /events/{id} should return 404 if event not found")
    void shouldReturnNotFoundWhenEventIdDoesNotExist() throws Exception {
        when(eventService.getEventById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/events/99"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).getEventById(99L);
    }

    @Test
    @DisplayName("POST /events should create a new event")
    void shouldCreateEvent() throws Exception {
        Event newEvent = new Event(null, "New Event", "Description",
                LocalDateTime.of(2025, 7, 11, 10, 0),
                LocalDateTime.of(2025, 7, 11, 11, 0),
                false);
        Event createdEvent = new Event(3L, "New Event", "Description",
                LocalDateTime.of(2025, 7, 11, 10, 0),
                LocalDateTime.of(2025, 7, 11, 11, 0),
                false);

        when(eventService.createEvent(any(Event.class))).thenReturn(createdEvent);

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.title").value("New Event"));

        verify(eventService, times(1)).createEvent(any(Event.class));
    }

    @Test
    @DisplayName("POST /events should return 400 if title is blank")
    void shouldReturnBadRequestWhenCreatingEventWithBlankTitle() throws Exception {
        Event invalidEvent = new Event(null, "", "Description",
                LocalDateTime.of(2025, 7, 11, 10, 0),
                LocalDateTime.of(2025, 7, 11, 11, 0),
                false);

        mockMvc.perform(MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isBadRequest()); // @Valid sollte hier zuschlagen

        verify(eventService, never()).createEvent(any(Event.class)); // Service sollte nicht aufgerufen werden
    }


    @Test
    @DisplayName("PUT /events/{id} should update an existing event")
    void shouldUpdateEvent() throws Exception {
        Event updatedEventDetails = new Event(1L, "Updated Title", "Updated Desc",
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 10, 0),
                true); // isCompleted auf true gesetzt

        when(eventService.updateEvent(eq(1L), any(Event.class))).thenReturn(updatedEventDetails);

        mockMvc.perform(MockMvcRequestBuilders.put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.isCompleted").value(true));

        verify(eventService, times(1)).updateEvent(eq(1L), any(Event.class));
    }

    @Test
    @DisplayName("PUT /events/{id} should return 404 if event not found for update")
    void shouldReturnNotFoundWhenUpdatingNonExistentEvent() throws Exception {
        Event updatedEventDetails = new Event(99L, "Non Existent", "Desc",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), false);

        // Mocken Sie die Ausnahme, die vom Service geworfen wird
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Event not found with ID: 99"))
                .when(eventService).updateEvent(eq(99L), any(Event.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/events/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDetails)))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).updateEvent(eq(99L), any(Event.class));
    }

    @Test
    @DisplayName("DELETE /events/{id} should delete an event")
    void shouldDeleteEvent() throws Exception {
        doNothing().when(eventService).deleteEvent(1L); // Simuliert erfolgreiches Löschen

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/1"))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(eventService, times(1)).deleteEvent(1L);
    }

    @Test
    @DisplayName("DELETE /events/{id} should return 404 if event not found for delete")
    void shouldReturnNotFoundWhenDeletingNonExistentEvent() throws Exception {
        // Mocken Sie die Ausnahme, die vom Service geworfen wird
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Event not found with ID: 99"))
                .when(eventService).deleteEvent(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/events/99"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).deleteEvent(99L);
    }

    @Test
    @DisplayName("PATCH /events/{id}/complete should update completion status")
    void shouldUpdateCompletionStatus() throws Exception {
        Event updatedEvent = new Event(1L, "Meeting", "Team Meeting",
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 10, 0),
                true); // Neuer Status

        when(eventService.updateEventCompletionStatus(eq(1L), eq(true))).thenReturn(updatedEvent);

        mockMvc.perform(MockMvcRequestBuilders.patch("/events/1/complete")
                        .param("isCompleted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isCompleted").value(true));

        verify(eventService, times(1)).updateEventCompletionStatus(eq(1L), eq(true));
    }

    @Test
    @DisplayName("PATCH /events/{id}/complete should return 404 if event not found")
    void shouldReturnNotFoundWhenUpdatingCompletionStatusForNonExistentEvent() throws Exception {
        // Mocken Sie die Ausnahme, die vom Service geworfen wird
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Event not found with ID: 99"))
                .when(eventService).updateEventCompletionStatus(eq(99L), anyBoolean());

        mockMvc.perform(MockMvcRequestBuilders.patch("/events/99/complete")
                        .param("isCompleted", "true"))
                .andExpect(status().isNotFound());

        verify(eventService, times(1)).updateEventCompletionStatus(eq(99L), anyBoolean());
    }
}