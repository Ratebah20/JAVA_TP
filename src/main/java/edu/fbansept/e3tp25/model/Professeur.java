package edu.fbansept.e3tp25.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("PROFESSEUR")
public class Professeur extends Utilisateur {

    protected int anneesExperience;

}
