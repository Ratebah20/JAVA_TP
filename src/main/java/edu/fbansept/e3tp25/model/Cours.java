package edu.fbansept.e3tp25.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_cours")
public abstract class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(nullable = false)
    protected String nom;

    @Column(nullable = false)
    protected LocalDateTime debut;

    @Column(nullable = false)
    protected Integer duree; // en minutes

    @ManyToMany
    @JoinTable(
            name = "cours_etudiant",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    protected List<Etudiant> etudiantList = new ArrayList<>();

    @ManyToOne(optional = false)
    protected Professeur professeur;


    // Méthode utilitaire pour calculer la fin du cours
    public LocalDateTime getFin() {
        return debut.plusMinutes(duree);
    }
}
