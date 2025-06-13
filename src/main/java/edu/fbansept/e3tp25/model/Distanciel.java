package edu.fbansept.e3tp25.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("DISTANCIEL")
@Getter
@Setter
public class Distanciel extends Cours {
    @Column(name = "lien_reunion")
    private String lienReunion;
}
