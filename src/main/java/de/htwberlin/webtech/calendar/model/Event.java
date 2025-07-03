package de.htwberlin.webtech.calendar.model;

import jakarta.persistence.Column;
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

@Entity // Markiert diese Klasse als JPA-Entität, die einer Datenbanktabelle zugeordnet ist
@Data // Lombok-Annotation: Generiert automatisch Getter, Setter, toString, equals und hashCode Methoden
@AllArgsConstructor // Lombok-Annotation: Generiert einen Konstruktor mit allen Feldern
@NoArgsConstructor // Lombok-Annotation: Generiert einen parameterlosen Konstruktor (wird von JPA/Hibernate benötigt)
public class Event {

    @Id // Markiert dieses Feld als Primärschlüssel der Entität
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Konfiguriert die automatische Generierung des Primärschlüssels durch die Datenbank
    private Long id;

    @NotBlank(message = "Title is mandatory and cannot be empty") // Validierungs-Constraint: Der Titel darf nicht null oder leer sein
    private String title;

    private String description; // Beschreibung des Events (optional)

    @NotNull(message = "Start date and time are mandatory") // Validierungs-Constraint: Das Startdatum darf nicht null sein
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time are mandatory") // Validierungs-Constraint: Das Enddatum darf nicht null sein
    private LocalDateTime endDateTime;

    @Column(name = "is_completed") // Definiert den Spaltennamen in der Datenbank, optional bei Namenskonvention
    private Boolean isCompleted = false; // Statusfeld: Gibt an, ob ein Event abgeschlossen ist. Standardmäßig false.
}