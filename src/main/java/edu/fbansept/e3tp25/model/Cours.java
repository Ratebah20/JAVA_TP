package edu.fbansept.e3tp25.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @ManyToMany
    @JoinTable(
            name = "cours_etudiant",
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    protected List<Etudiant> etudiantList = new ArrayList<>();

    @ManyToOne(optional = false)
    protected Professeur professeur;

}
