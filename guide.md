# Guide d'implémentation - Gestion avancée des cours

## Vue d'ensemble des modifications

Ce guide détaille l'implémentation des nouvelles fonctionnalités pour la gestion des cours avec :
- Gestion des types de cours (présentiel/distanciel)
- Gestion des salles pour les cours en présentiel
- Gestion des conflits d'horaires
- Ajout du rôle Administrateur
- Tests d'intégration complets

## Étape 1 : Création des nouvelles entités

### 1.1 Créer l'entité Salle

```java
// model/Salle.java
package edu.fbansept.e3tp25.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

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
    private List<Presentiel> coursPresentiels;
}
```

### 1.2 Créer l'entité Administrateur

```java
// model/Administrateur.java
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
```

## Étape 2 : Transformer Cours en classe abstraite avec héritage

### 2.1 Modifier la classe Cours

```java
// model/Cours.java
package edu.fbansept.e3tp25.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_cours")
@Getter
@Setter
public abstract class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private LocalDateTime debut;
    
    @Column(nullable = false)
    private Integer duree; // en minutes
    
    @ManyToMany
    @JoinTable(
        name = "cours_etudiant",
        joinColumns = @JoinColumn(name = "cours_id"),
        inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    private List<Etudiant> etudiantList;
    
    @ManyToOne(optional = false)
    private Professeur professeur;
    
    // Méthode utilitaire pour calculer la fin du cours
    public LocalDateTime getFin() {
        return debut.plusMinutes(duree);
    }
}
```

### 2.2 Créer la classe Distanciel

```java
// model/Distanciel.java
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
```

### 2.3 Créer la classe Presentiel

```java
// model/Presentiel.java
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
```

## Étape 3 : Créer les DAOs

### 3.1 DAO pour Salle

```java
// dao/SalleDao.java
package edu.fbansept.e3tp25.dao;

import edu.fbansept.e3tp25.model.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalleDao extends JpaRepository<Salle, Integer> {
    
    @Query("SELECT s FROM Salle s WHERE s.capacite >= :capaciteRequise")
    List<Salle> findByCapaciteGreaterThanEqual(@Param("capaciteRequise") Integer capaciteRequise);
    
    @Query("SELECT s FROM Salle s WHERE s.id NOT IN " +
           "(SELECT p.salle.id FROM Presentiel p WHERE " +
           "p.debut < :fin AND :debut < (p.debut + p.duree * 60000))")
    List<Salle> findSallesDisponibles(@Param("debut") LocalDateTime debut, 
                                      @Param("fin") LocalDateTime fin);
}
```

### 3.2 DAO pour Administrateur

```java
// dao/AdministrateurDao.java
package edu.fbansept.e3tp25.dao;

import edu.fbansept.e3tp25.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrateurDao extends JpaRepository<Administrateur, Integer> {
}
```

### 3.3 Modifier CoursDao

```java
// dao/CoursDao.java
package edu.fbansept.e3tp25.dao;

import edu.fbansept.e3tp25.model.Cours;
import edu.fbansept.e3tp25.model.Etudiant;
import edu.fbansept.e3tp25.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoursDao extends JpaRepository<Cours, Integer> {
    
    @Query("SELECT c FROM Cours c WHERE c.professeur = :professeur AND " +
           "c.debut < :fin AND :debut < (c.debut + c.duree * 60000)")
    List<Cours> findConflictsProfesseur(@Param("professeur") Professeur professeur,
                                        @Param("debut") LocalDateTime debut,
                                        @Param("fin") LocalDateTime fin);
    
    @Query("SELECT c FROM Cours c JOIN c.etudiantList e WHERE e = :etudiant AND " +
           "c.debut < :fin AND :debut < (c.debut + c.duree * 60000)")
    List<Cours> findConflictsEtudiant(@Param("etudiant") Etudiant etudiant,
                                      @Param("debut") LocalDateTime debut,
                                      @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(p) > 0 FROM Presentiel p WHERE p.salle.id = :salleId AND " +
           "p.debut < :fin AND :debut < (p.debut + p.duree * 60000)")
    boolean isSalleOccupee(@Param("salleId") Integer salleId,
                           @Param("debut") LocalDateTime debut,
                           @Param("fin") LocalDateTime fin);
}
```

## Étape 4 : Créer les DTOs pour les requêtes

### 4.1 DTO pour création de cours

```java
// dto/CreateCoursDto.java
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
```

### 4.2 DTO pour les conflits

```java
// dto/ConflitDto.java
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
```

## Étape 5 : Créer le service de gestion des cours

```java
// service/CoursService.java
package edu.fbansept.e3tp25.service;

import edu.fbansept.e3tp25.dao.*;
import edu.fbansept.e3tp25.dto.*;
import edu.fbansept.e3tp25.model.*;
import edu.fbansept.e3tp25.exception.ConflitHoraireException;
import edu.fbansept.e3tp25.exception.CapaciteSalleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CoursService {
    
    @Autowired
    private CoursDao coursDao;
    
    @Autowired
    private EtudiantDao etudiantDao;
    
    @Autowired
    private ProfesseurDao professeurDao;
    
    @Autowired
    private SalleDao salleDao;
    
    public Cours creerCours(CreateCoursDto dto) {
        // Vérifier les conflits
        List<ConflitDto> conflits = verifierConflits(dto);
        if (!conflits.isEmpty()) {
            throw new ConflitHoraireException("Conflits détectés", conflits);
        }
        
        // Récupérer les entités
        Professeur professeur = professeurDao.findById(dto.getProfesseurId())
            .orElseThrow(() -> new RuntimeException("Professeur non trouvé"));
        
        List<Etudiant> etudiants = etudiantDao.findAllById(dto.getEtudiantIds());
        
        // Créer le cours selon le type
        Cours cours;
        if ("PRESENTIEL".equals(dto.getTypeCours())) {
            Presentiel presentiel = new Presentiel();
            Salle salle = salleDao.findById(dto.getSalleId())
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));
            
            // Vérifier la capacité
            if (etudiants.size() > salle.getCapacite()) {
                throw new CapaciteSalleException(
                    String.format("La salle %s a une capacité de %d places, mais %d étudiants sont inscrits",
                    salle.getNom(), salle.getCapacite(), etudiants.size())
                );
            }
            
            presentiel.setSalle(salle);
            cours = presentiel;
        } else {
            Distanciel distanciel = new Distanciel();
            distanciel.setLienReunion(dto.getLienReunion());
            cours = distanciel;
        }
        
        // Configurer les propriétés communes
        cours.setNom(dto.getNom());
        cours.setDebut(dto.getDebut());
        cours.setDuree(dto.getDuree());
        cours.setProfesseur(professeur);
        cours.setEtudiantList(etudiants);
        
        return coursDao.save(cours);
    }
    
    private List<ConflitDto> verifierConflits(CreateCoursDto dto) {
        List<ConflitDto> conflits = new ArrayList<>();
        LocalDateTime debut = dto.getDebut();
        LocalDateTime fin = debut.plusMinutes(dto.getDuree());
        
        // Vérifier conflit professeur
        Professeur professeur = professeurDao.findById(dto.getProfesseurId()).orElse(null);
        if (professeur != null) {
            List<Cours> conflitsProfesseur = coursDao.findConflictsProfesseur(professeur, debut, fin);
            if (!conflitsProfesseur.isEmpty()) {
                conflits.add(new ConflitDto(
                    "PROFESSEUR",
                    String.format("Le professeur %s a déjà un cours à cette période", professeur.getEmail()),
                    conflitsProfesseur
                ));
            }
        }
        
        // Vérifier conflits étudiants
        List<Etudiant> etudiants = etudiantDao.findAllById(dto.getEtudiantIds());
        for (Etudiant etudiant : etudiants) {
            List<Cours> conflitsEtudiant = coursDao.findConflictsEtudiant(etudiant, debut, fin);
            if (!conflitsEtudiant.isEmpty()) {
                conflits.add(new ConflitDto(
                    "ETUDIANT",
                    String.format("L'étudiant %s a déjà un cours à cette période", etudiant.getEmail()),
                    conflitsEtudiant
                ));
            }
        }
        
        // Vérifier conflit salle (si présentiel)
        if ("PRESENTIEL".equals(dto.getTypeCours()) && dto.getSalleId() != null) {
            boolean salleOccupee = coursDao.isSalleOccupee(dto.getSalleId(), debut, fin);
            if (salleOccupee) {
                Salle salle = salleDao.findById(dto.getSalleId()).orElse(null);
                conflits.add(new ConflitDto(
                    "SALLE",
                    String.format("La salle %s est déjà occupée à cette période", 
                        salle != null ? salle.getNom() : "inconnue"),
                    null
                ));
            }
        }
        
        return conflits;
    }
}
```

## Étape 6 : Créer les exceptions personnalisées

### 6.1 Exception pour les conflits d'horaire

```java
// exception/ConflitHoraireException.java
package edu.fbansept.e3tp25.exception;

import edu.fbansept.e3tp25.dto.ConflitDto;
import lombok.Getter;
import java.util.List;

@Getter
public class ConflitHoraireException extends RuntimeException {
    private final List<ConflitDto> conflits;
    
    public ConflitHoraireException(String message, List<ConflitDto> conflits) {
        super(message);
        this.conflits = conflits;
    }
}
```

### 6.2 Exception pour la capacité de salle

```java
// exception/CapaciteSalleException.java
package edu.fbansept.e3tp25.exception;

public class CapaciteSalleException extends RuntimeException {
    public CapaciteSalleException(String message) {
        super(message);
    }
}
```

## Étape 7 : Créer les contrôleurs

### 7.1 Contrôleur pour les salles

```java
// controller/SalleController.java
package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.SalleDao;
import edu.fbansept.e3tp25.model.Salle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/salle")
@CrossOrigin
public class SalleController {
    
    @Autowired
    private SalleDao salleDao;
    
    @GetMapping("/liste")
    public List<Salle> liste() {
        return salleDao.findAll();
    }
    
    @GetMapping("/{id}")
    public Salle get(@PathVariable Integer id) {
        return salleDao.findById(id).orElse(null);
    }
    
    @PostMapping
    public Salle create(@RequestBody Salle salle) {
        return salleDao.save(salle);
    }
    
    @GetMapping("/disponibles")
    public List<Salle> getSallesDisponibles(
            @RequestParam String debut,
            @RequestParam String fin,
            @RequestParam(required = false) Integer capaciteMin) {
        // Implémenter la logique de recherche
        return salleDao.findAll(); // Simplification
    }
}
```

### 7.2 Modifier le contrôleur des cours

```java
// controller/CoursController.java
package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.CoursDao;
import edu.fbansept.e3tp25.dto.CreateCoursDto;
import edu.fbansept.e3tp25.model.Cours;
import edu.fbansept.e3tp25.service.CoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cours")
@CrossOrigin
public class CoursController {
    
    @Autowired
    private CoursDao coursDao;
    
    @Autowired
    private CoursService coursService;
    
    @GetMapping("/liste")
    public List<Cours> liste() {
        return coursDao.findAll();
    }
    
    @GetMapping("/{id}")
    public Cours get(@PathVariable Integer id) {
        return coursDao.findById(id).orElse(null);
    }
    
    @PostMapping("/creer")
    public ResponseEntity<?> creerCours(@RequestBody CreateCoursDto dto) {
        try {
            Cours cours = coursService.creerCours(dto);
            return ResponseEntity.ok(cours);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        try {
            coursDao.deleteById(id);
            return ResponseEntity.ok("Cours supprimé");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Cours non trouvé");
        }
    }
}
```

## Étape 8 : Tests d'intégration

### 8.1 Configuration de test

```java
// test/java/edu/fbansept/e3tp25/integration/BaseIntegrationTest.java
package edu.fbansept.e3tp25.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {
    // Configuration commune pour tous les tests
}
```

### 8.2 Tests pour la création de cours

```java
// test/java/edu/fbansept/e3tp25/integration/CoursIntegrationTest.java
package edu.fbansept.e3tp25.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fbansept.e3tp25.dto.CreateCoursDto;
import edu.fbansept.e3tp25.model.*;
import edu.fbansept.e3tp25.dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class CoursIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProfesseurDao professeurDao;
    
    @Autowired
    private EtudiantDao etudiantDao;
    
    @Autowired
    private SalleDao salleDao;
    
    @Autowired
    private CoursDao coursDao;
    
    private Professeur professeur;
    private Etudiant etudiant1, etudiant2;
    private Salle salle;
    
    @BeforeEach
    void setUp() {
        // Créer les données de test
        professeur = new Professeur();
        professeur.setEmail("prof@test.com");
        professeur.setPassword("password");
        professeur.setAnneesExperience(5);
        professeur = professeurDao.save(professeur);
        
        etudiant1 = new Etudiant();
        etudiant1.setEmail("etudiant1@test.com");
        etudiant1.setPassword("password");
        etudiant1.setDateNaissance(LocalDate.of(2000, 1, 1));
        etudiant1 = etudiantDao.save(etudiant1);
        
        etudiant2 = new Etudiant();
        etudiant2.setEmail("etudiant2@test.com");
        etudiant2.setPassword("password");
        etudiant2.setDateNaissance(LocalDate.of(2001, 1, 1));
        etudiant2 = etudiantDao.save(etudiant2);
        
        salle = new Salle();
        salle.setNom("Salle A101");
        salle.setCapacite(30);
        salle = salleDao.save(salle);
    }
    
    @Test
    void testCreerCoursPresentiel_Success() throws Exception {
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Mathématiques");
        dto.setDebut(LocalDateTime.now().plusDays(1));
        dto.setDuree(120);
        dto.setProfesseurId(professeur.getId());
        dto.setEtudiantIds(Arrays.asList(etudiant1.getId(), etudiant2.getId()));
        dto.setTypeCours("PRESENTIEL");
        dto.setSalleId(salle.getId());
        
        mockMvc.perform(post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Mathématiques")))
                .andExpect(jsonPath("$.professeur.id", is(professeur.getId())))
                .andExpect(jsonPath("$.etudiantList", hasSize(2)));
    }
    
    @Test
    void testCreerCoursDistanciel_Success() throws Exception {
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Programmation Java");
        dto.setDebut(LocalDateTime.now().plusDays(1));
        dto.setDuree(90);
        dto.setProfesseurId(professeur.getId());
        dto.setEtudiantIds(Arrays.asList(etudiant1.getId()));
        dto.setTypeCours("DISTANCIEL");
        dto.setLienReunion("https://zoom.us/j/123456789");
        
        mockMvc.perform(post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom", is("Programmation Java")))
                .andExpect(jsonPath("$.lienReunion", is("https://zoom.us/j/123456789")));
    }
    
    @Test
    void testCreerCours_ConflitProfesseur() throws Exception {
        // Créer un cours existant
        LocalDateTime debut = LocalDateTime.now().plusDays(1);
        Presentiel coursExistant = new Presentiel();
        coursExistant.setNom("Cours existant");
        coursExistant.setDebut(debut);
        coursExistant.setDuree(120);
        coursExistant.setProfesseur(professeur);
        coursExistant.setSalle(salle);
        coursDao.save(coursExistant);
        
        // Tenter de créer un cours en conflit
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Nouveau cours");
        dto.setDebut(debut.plusMinutes(30)); // Chevauchement
        dto.setDuree(60);
        dto.setProfesseurId(professeur.getId());
        dto.setEtudiantIds(Arrays.asList(etudiant1.getId()));
        dto.setTypeCours("DISTANCIEL");
        dto.setLienReunion("https://meet.google.com/abc-defg-hij");
        
        mockMvc.perform(post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Conflits détectés")));
    }
    
    @Test
    void testCreerCours_CapaciteSalleDepassee() throws Exception {
        // Créer une petite salle
        Salle petiteSalle = new Salle();
        petiteSalle.setNom("Petite salle");
        petiteSalle.setCapacite(1); // Capacité de 1 seule personne
        petiteSalle = salleDao.save(petiteSalle);
        
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours trop grand");
        dto.setDebut(LocalDateTime.now().plusDays(1));
        dto.setDuree(60);
        dto.setProfesseurId(professeur.getId());
        dto.setEtudiantIds(Arrays.asList(etudiant1.getId(), etudiant2.getId())); // 2 étudiants
        dto.setTypeCours("PRESENTIEL");
        dto.setSalleId(petiteSalle.getId());
        
        mockMvc.perform(post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("capacité")));
    }
    
    @Test
    void testCreerCours_ConflitSalle() throws Exception {
        // Créer un cours existant dans la salle
        LocalDateTime debut = LocalDateTime.now().plusDays(1);
        Presentiel coursExistant = new Presentiel();
        coursExistant.setNom("Cours existant");
        coursExistant.setDebut(debut);
        coursExistant.setDuree(120);
        coursExistant.setProfesseur(professeur);
        coursExistant.setSalle(salle);
        coursDao.save(coursExistant);
        
        // Créer un autre professeur
        Professeur autreProfesseur = new Professeur();
        autreProfesseur.setEmail("autre.prof@test.com");
        autreProfesseur.setPassword("password");
        autreProfesseur.setAnneesExperience(3);
        autreProfesseur = professeurDao.save(autreProfesseur);
        
        // Tenter de créer un cours dans la même salle au même moment
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours en conflit");
        dto.setDebut(debut.plusMinutes(30)); // Chevauchement
        dto.setDuree(60);
        dto.setProfesseurId(autreProfesseur.getId());
        dto.setEtudiantIds(Arrays.asList(etudiant2.getId()));
        dto.setTypeCours("PRESENTIEL");
        dto.setSalleId(salle.getId());
        
        mockMvc.perform(post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString("Conflits détectés")));
    }
}
```

## Étape 9 : Migration de la base de données

### 9.1 Script SQL de migration

```sql
-- migration.sql

-- Modifier la table cours pour l'héritage
ALTER TABLE cours ADD COLUMN type_cours VARCHAR(20) NOT NULL DEFAULT 'PRESENTIEL';
ALTER TABLE cours ADD COLUMN debut DATETIME NOT NULL;
ALTER TABLE cours ADD COLUMN duree INT NOT NULL;

-- Créer la table salle
CREATE TABLE salle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    capacite INT NOT NULL
);

-- Créer la table distanciel
CREATE TABLE distanciel (
    id INT PRIMARY KEY,
    lien_reunion VARCHAR(500),
    FOREIGN KEY (id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Créer la table presentiel  
CREATE TABLE presentiel (
    id INT PRIMARY KEY,
    salle_id INT,
    FOREIGN KEY (id) REFERENCES cours(id) ON DELETE CASCADE,
    FOREIGN KEY (salle_id) REFERENCES salle(id)
);

-- Ajouter le type administrateur
ALTER TABLE utilisateur MODIFY COLUMN nom_role ENUM('ETUDIANT', 'PROFESSEUR', 'ADMINISTRATEUR');

-- Créer la table administrateur
CREATE TABLE administrateur (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES utilisateur(id) ON DELETE CASCADE
);

-- Insérer des salles de test
INSERT INTO salle (nom, capacite) VALUES 
('Amphithéâtre A', 200),
('Salle TD 101', 30),
('Salle TP Info 1', 20),
('Salle de conférence', 50);
```

### 9.2 Modifier le fichier de données de test

```sql
-- data-donnees-test.sql (à ajouter)

-- Salles
INSERT INTO salle (id, nom, capacite) VALUES 
(1, 'Amphithéâtre A', 200),
(2, 'Salle TD 101', 30),
(3, 'Salle TP Info 1', 20),
(4, 'Salle de conférence', 50);

-- Administrateur
INSERT INTO utilisateur (id, email, password, nom_role) VALUES 
(5, 'admin@universite.fr', '$2a$10$hashedpassword', 'ADMINISTRATEUR');
INSERT INTO administrateur (id) VALUES (5);

-- Cours de test (optionnel)
-- Cours présentiel
INSERT INTO cours (id, nom, debut, duree, professeur_id, type_cours) VALUES 
(1, 'Mathématiques Avancées', '2025-06-16 09:00:00', 120, 3, 'PRESENTIEL');
INSERT INTO presentiel (id, salle_id) VALUES (1, 1);

-- Cours distanciel
INSERT INTO cours (id, nom, debut, duree, professeur_id, type_cours) VALUES 
(2, 'Programmation Web', '2025-06-16 14:00:00', 90, 4, 'DISTANCIEL');
INSERT INTO distanciel (id, lien_reunion) VALUES (2, 'https://zoom.us/j/987654321');
```

## Étape 10 : Configuration et finalisation

### 10.1 Gestion globale des exceptions

```java
// exception/GlobalExceptionHandler.java
package edu.fbansept.e3tp25.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ConflitHoraireException.class)
    public ResponseEntity<Map<String, Object>> handleConflitHoraire(ConflitHoraireException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());
        response.put("conflits", e.getConflits());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(CapaciteSalleException.class)
    public ResponseEntity<Map<String, Object>> handleCapaciteSalle(CapaciteSalleException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
```

### 10.2 Configuration pour les tests

```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
spring.jpa.show-sql=true
```

## Points importants à retenir

1. **Validation des conflits** : Le système vérifie automatiquement les conflits d'horaires pour les professeurs, étudiants et salles
2. **Gestion de la capacité** : La capacité des salles est vérifiée avant l'attribution
3. **Flexibilité** : Support des cours en présentiel et distanciel
4. **Extensibilité** : Architecture permettant l'ajout facile de nouvelles fonctionnalités
5. **Tests complets** : Tests d'intégration couvrant tous les cas d'usage principaux

## Commandes pour tester

```bash
# Lancer les tests
mvn test

# Lancer uniquement les tests d'intégration
mvn test -Dtest=CoursIntegrationTest

# Lancer l'application
mvn spring-boot:run
```

## Exemples de requêtes API

### Créer un cours présentiel
```bash
curl -X POST http://localhost:8080/api/cours/creer \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Algorithmique",
    "debut": "2025-06-20T10:00:00",
    "duree": 120,
    "professeurId": 3,
    "etudiantIds": [1, 2],
    "typeCours": "PRESENTIEL",
    "salleId": 1
  }'
```

### Créer un cours distanciel
```bash
curl -X POST http://localhost:8080/api/cours/creer \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Base de données",
    "debut": "2025-06-20T14:00:00",
    "duree": 90,
    "professeurId": 4,
    "etudiantIds": [1],
    "typeCours": "DISTANCIEL",
    "lienReunion": "https://teams.microsoft.com/l/meetup-join/..."
  }'
```