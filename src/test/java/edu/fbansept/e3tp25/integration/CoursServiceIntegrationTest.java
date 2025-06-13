package edu.fbansept.e3tp25.integration;

import edu.fbansept.e3tp25.dao.EtudiantDao;
import edu.fbansept.e3tp25.dao.ProfesseurDao;
import edu.fbansept.e3tp25.dao.SalleDao;
import edu.fbansept.e3tp25.dto.CreateCoursDto;
import edu.fbansept.e3tp25.exception.CapaciteSalleException;
import edu.fbansept.e3tp25.exception.ConflitHoraireException;
import edu.fbansept.e3tp25.model.Cours;
import edu.fbansept.e3tp25.model.Distanciel;
import edu.fbansept.e3tp25.model.Presentiel;
import edu.fbansept.e3tp25.service.CoursService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Utilise application-test.properties
@Transactional // Chaque test s'exécute dans une transaction qui est annulée à la fin
public class CoursServiceIntegrationTest {

    @Autowired
    private CoursService coursService;
    
    @Autowired
    private EtudiantDao etudiantDao;
    
    @Autowired
    private ProfesseurDao professeurDao;
    
    @Autowired
    private SalleDao salleDao;

    @Test
    public void testCreerCoursPresententiel_Success() {
        // Préparation des données
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours Test Intégration");
        dto.setDebut(LocalDateTime.now().plusDays(7)); // Cours dans une semaine
        dto.setDuree(90);
        dto.setProfesseurId(3); // ID du professeur1
        dto.setEtudiantIds(Arrays.asList(1, 2)); // Utilisez les IDs des étudiants existants dans les données de test
        dto.setTypeCours("PRESENTIEL");
        dto.setSalleId(1); // Utilisez l'ID d'une salle existante dans les données de test
        
        // Exécution du test
        Cours cours = coursService.creerCours(dto);
        
        // Vérifications
        assertNotNull(cours);
        assertTrue(cours instanceof Presentiel);
        assertEquals("Cours Test Intégration", cours.getNom());
        assertEquals(90, cours.getDuree());
        assertNotNull(((Presentiel) cours).getSalle());
        assertEquals(1, ((Presentiel) cours).getSalle().getId());
        assertEquals(2, cours.getEtudiantList().size());
        assertEquals(3, cours.getProfesseur().getId());
    }
    
    @Test
    public void testCreerCoursDistanciel_Success() {
        // Préparation des données
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours Distanciel Test");
        dto.setDebut(LocalDateTime.now().plusDays(7)); // Cours dans une semaine
        dto.setDuree(60);
        dto.setProfesseurId(4); // Utilisez l'ID d'un autre professeur existant dans les données de test
        dto.setEtudiantIds(Arrays.asList(1)); // Utilisez l'ID d'un étudiant existant dans les données de test
        dto.setTypeCours("DISTANCIEL");
        dto.setLienReunion("https://meeting-test.example.com/123");
        
        // Exécution du test
        Cours cours = coursService.creerCours(dto);
        
        // Vérifications
        assertNotNull(cours);
        assertTrue(cours instanceof Distanciel);
        assertEquals("Cours Distanciel Test", cours.getNom());
        assertEquals(60, cours.getDuree());
        assertEquals("https://meeting-test.example.com/123", ((Distanciel) cours).getLienReunion());
        assertEquals(1, cours.getEtudiantList().size());
        assertEquals(4, cours.getProfesseur().getId());
    }
    
    @Test
    public void testConflitProfesseur() {
        // Créer un cours à la même heure que le Cours Test 1 existant (ID 100, 2025-07-01 09:00:00, durée 90 min, prof ID 3)
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours En Conflit");
        dto.setDebut(LocalDateTime.of(2025, 7, 1, 9, 30)); // 30 min après le début du cours existant -> en conflit
        dto.setDuree(60);
        dto.setProfesseurId(3); // Même professeur que le cours existant
        dto.setEtudiantIds(Arrays.asList(2));
        dto.setTypeCours("DISTANCIEL");
        dto.setLienReunion("https://test.example.com");
        
        // Vérifier que l'exception est bien lancée
        assertThrows(ConflitHoraireException.class, () -> {
            coursService.creerCours(dto);
        });
    }
    
    @Test
    public void testCapaciteSalleDepassee() {
        // Créer un cours présentiel avec trop d'étudiants pour la salle ID 2 qui a une capacité de 15 places
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours Trop Grand");
        dto.setDebut(LocalDateTime.now().plusDays(10));
        dto.setDuree(120);
        dto.setProfesseurId(4);
        // Créer une liste de 20 étudiants (même si en réalité on n'en a que 2 dans notre jeu de test)
        // La validation se fait sur le nombre d'étudiants, pas sur le fait qu'ils existent réellement
        dto.setEtudiantIds(Arrays.asList(1, 2)); // On va simuler un cas où ces ID représentent plus d'étudiants
        dto.setTypeCours("PRESENTIEL");
        dto.setSalleId(2); // Salle avec capacité de 15
        
        // Pour ce test, nous allons modifier temporairement la capacité de la salle
        var salle = salleDao.findById(2).orElseThrow();
        salle.setCapacite(1); // Capacité insuffisante pour 2 étudiants
        salleDao.save(salle);
        
        // Vérifier que l'exception est bien lancée
        assertThrows(CapaciteSalleException.class, () -> {
            coursService.creerCours(dto);
        });
    }
}
