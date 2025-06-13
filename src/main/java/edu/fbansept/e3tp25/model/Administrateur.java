package edu.fbansept.e3tp25.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("ADMINISTRATEUR")
@Getter
@Setter
public class Administrateur extends Utilisateur {
    // Pas d'attributs supplémentaires pour l'instant
}
