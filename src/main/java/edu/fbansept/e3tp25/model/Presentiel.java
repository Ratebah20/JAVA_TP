package edu.fbansept.e3tp25.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("PRESENTIEL")
@Getter
@Setter
public class Presentiel extends Cours {
    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;
}
