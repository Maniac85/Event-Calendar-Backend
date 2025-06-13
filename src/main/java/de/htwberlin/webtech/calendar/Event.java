package de.htwberlin.webtech.calendar;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}