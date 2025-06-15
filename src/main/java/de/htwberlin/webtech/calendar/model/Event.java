package de.htwberlin.webtech.calendar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity // Markiert diese Klasse als JPA-Entität
@Data // Lombok-Annotation für Getter, Setter, toString, equals, hashCode
@AllArgsConstructor
@NoArgsConstructor // (Hibernate benötigt einen No-Args Konstruktor)
public class Event {

    @Id // Markiert dies als Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Generiert die ID automatisch
    private Long id;

    @NotBlank(message = "Title is mandatory and cannot be empty") // Der Titel darf nicht null oder leer sein
    private String title;

    private String description; // Beschreibung darf weiterhin null/leer sein

    @NotNull(message = "Start date and time are mandatory") // Das Startdatum darf nicht null sein
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time are mandatory") // Das Enddatum darf nicht null sein
    private LocalDateTime endDateTime;
}
