package edu.fbansept.e3tp25.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateCoursDto {
    private String nom;
    private LocalDateTime debut;
    private Integer duree;
    private Integer professeurId;
    private List<Integer> etudiantIds;
    private String typeCours; // "PRESENTIEL" ou "DISTANCIEL"
    private Integer salleId; // pour cours présentiel
    private String lienReunion; // pour cours distanciel
}
