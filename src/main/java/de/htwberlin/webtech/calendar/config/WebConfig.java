package de.htwberlin.webtech.calendar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // Markiert diese Klasse als Spring Konfigurationsklasse
public class WebConfig implements WebMvcConfigurer {

    /**
     * Konfiguriert CORS (Cross-Origin Resource Sharing) für die Anwendung.
     * Erlaubt Anfragen von spezifischen Ursprüngen (Domains) zu den definierten Endpunkten.
     * Dies ist notwendig, damit dein Frontend (das auf einer anderen Domain läuft) mit deinem Backend kommunizieren kann.
     * @param registry Der CorsRegistry-Objekt, um CORS-Regeln hinzuzufügen.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Konfiguriert CORS-Regeln für alle Endpunkte unter "/events/**"
        registry.addMapping("/events/**")
                // Erlaubt Anfragen von diesen Ursprüngen
                .allowedOrigins(
                        "http://localhost:5173", // Lokaler Entwicklungsserver des Frontends
                        "https://event-calendar-frontend.onrender.com" // Produkter Frontendl
                )
                // Erlaubt die spezifizierten HTTP-Methoden für Cross-Origin-Anfragen
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // PATCH für Status-Update hinzugefügt
                // Erlaubt alle Header in den Anfragen
                .allowedHeaders("*")
                // Erlaubt das Senden von Cookies und Authentifizierungs-Headern
                .allowCredentials(true);
    }
}