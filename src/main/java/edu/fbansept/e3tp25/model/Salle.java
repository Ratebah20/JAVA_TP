package edu.fbansept.e3tp25.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private Integer capacite;
    
    @OneToMany(mappedBy = "salle")
    private List<Presentiel> coursPresentiels = new ArrayList<>();
}
