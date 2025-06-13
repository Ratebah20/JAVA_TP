package edu.fbansept.e3tp25.model;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@DiscriminatorValue("ETUDIANT")
public class Etudiant extends Utilisateur {

    LocalDate dateNaissance;

}
