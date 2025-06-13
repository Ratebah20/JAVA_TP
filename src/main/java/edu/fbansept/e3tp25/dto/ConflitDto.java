package edu.fbansept.e3tp25.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConflitDto {
    private String type; // "PROFESSEUR", "ETUDIANT", "SALLE"
    private String message;
    private Object details;
}
