package edu.fbansept.e3tp25.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fbansept.e3tp25.dao.*;
import edu.fbansept.e3tp25.dto.CreateCoursDto;
import edu.fbansept.e3tp25.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CoursControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CoursDao coursDao;
    
    @Autowired
    private EtudiantDao etudiantDao;
    
    @Autowired
    private ProfesseurDao professeurDao;
    
    @Autowired
    private SalleDao salleDao;
    
    private Etudiant etudiant1;
    private Etudiant etudiant2;
    private Professeur professeur1;
    private Professeur professeur2;
    private Salle salleA;
    private Salle salleB;
    
    @BeforeEach
    public void setUp() {
        // Créer des données de test
        
        // Création des utilisateurs
        etudiant1 = new Etudiant();
        etudiant1.setEmail("etudiant.test1@test.com");
        etudiant1.setPassword("test123");
        etudiant1.setDateNaissance(LocalDate.of(2000, 5, 15));
        etudiantDao.save(etudiant1);
        
        etudiant2 = new Etudiant();
        etudiant2.setEmail("etudiant.test2@test.com");
        etudiant2.setPassword("test123");
        etudiant2.setDateNaissance(LocalDate.of(2001, 3, 20));
        etudiantDao.save(etudiant2);
        
        professeur1 = new Professeur();
        professeur1.setEmail("prof.test1@test.com");
        professeur1.setPassword("test123");
        professeur1.setAnneesExperience(5);
        professeurDao.save(professeur1);
        
        professeur2 = new Professeur();
        professeur2.setEmail("prof.test2@test.com");
        professeur2.setPassword("test123");
        professeur2.setAnneesExperience(8);
        professeurDao.save(professeur2);
        
        // Création des salles
        salleA = new Salle();
        salleA.setNom("Salle de test A");
        salleA.setCapacite(30);
        salleDao.save(salleA);
        
        salleB = new Salle();
        salleB.setNom("Salle de test B");
        salleB.setCapacite(15);
        salleDao.save(salleB);
        
        // Créer quelques cours
        Presentiel cours1 = new Presentiel();
        cours1.setNom("Cours Test API 1");
        cours1.setDebut(LocalDateTime.of(2025, 10, 1, 9, 0));
        cours1.setDuree(90);
        cours1.setProfesseur(professeur1);
        cours1.setEtudiantList(List.of(etudiant1, etudiant2));
        cours1.setSalle(salleA);
        coursDao.save(cours1);
        
        Presentiel cours2 = new Presentiel();
        cours2.setNom("Cours Test API 2");
        cours2.setDebut(LocalDateTime.of(2025, 10, 2, 14, 0));
        cours2.setDuree(120);
        cours2.setProfesseur(professeur2);
        cours2.setEtudiantList(List.of(etudiant1));
        cours2.setSalle(salleB);
        coursDao.save(cours2);
        
        Distanciel cours3 = new Distanciel();
        cours3.setNom("Cours Test API 3");
        cours3.setDebut(LocalDateTime.of(2025, 10, 3, 10, 0));
        cours3.setDuree(60);
        cours3.setProfesseur(professeur1);
        cours3.setEtudiantList(List.of(etudiant2));
        cours3.setLienReunion("https://test-reunion.com/123");
        coursDao.save(cours3);
    }

    @Test
    public void testGetAllCours() throws Exception {
        // Test de récupération de tous les cours
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/cours/liste")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
                
        // Vérifions simplement que la réponse n'est pas vide et contient du contenu JSON
        String content = result.getResponse().getContentAsString();
        assertTrue(content.length() > 2, "Le résultat JSON ne doit pas être vide");
        // Vérifier que nous avons un tableau JSON (commence par [ et finit par ])
        assertTrue(content.startsWith("["), "Le résultat doit être un tableau JSON");
        assertTrue(content.endsWith("]"), "Le résultat doit être un tableau JSON");
    }

    @Test
    public void testGetAllPresentiel() throws Exception {
        // Test de récupération des cours présentiels uniquement
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/cours/presentiel/liste")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
                
        // Vérifions simplement que la réponse est un tableau JSON valide
        String content = result.getResponse().getContentAsString();
        assertTrue(content.length() > 2, "Le résultat JSON ne doit pas être vide");
        assertTrue(content.startsWith("["), "Le résultat doit être un tableau JSON");
        assertTrue(content.endsWith("]"), "Le résultat doit être un tableau JSON");
        
        // Vérifions qu'il s'agit de cours présentiels (mais sans vérifier le contenu exact)
        assertFalse(content.contains("lienReunion"), "Le résultat ne doit pas contenir de lien de réunion (distanciel)");
    }

    @Test
    public void testGetAllDistanciel() throws Exception {
        // Test de récupération des cours distanciels
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cours/distanciel/liste")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray()); // Vérifier qu'on obtient un tableau
    }

    @Test
    public void testCreerCoursPresentiel() throws Exception {
        // Création d'un DTO pour un nouveau cours présentiel
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours Test Nouveau");
        // Date sans conflit avec les cours existants
        dto.setDebut(LocalDateTime.of(2025, 12, 15, 14, 0)); 
        dto.setDuree(120);
        dto.setProfesseurId(professeur2.getId());
        dto.setEtudiantIds(Arrays.asList(etudiant1.getId(), etudiant2.getId()));
        dto.setTypeCours("PRESENTIEL");
        dto.setSalleId(salleA.getId());

        // Conversion du DTO en JSON
        String dtoJson = objectMapper.writeValueAsString(dto);

        // Envoi de la requête POST et vérification des résultats
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        // Vérification du contenu de la réponse
        String jsonResponse = result.getResponse().getContentAsString();
        assertTrue(jsonResponse.contains("Cours Test Nouveau"));
    }

    @Test
    public void testConflitProfesseurRetourne409() throws Exception {
        // Création d'un DTO pour un cours en conflit avec l'emploi du temps d'un professeur
        CreateCoursDto dto = new CreateCoursDto();
        dto.setNom("Cours En Conflit Professeur");
        dto.setDebut(LocalDateTime.of(2025, 10, 1, 9, 15)); // En conflit avec cours1 (9h00-10h30)
        dto.setDuree(60);
        dto.setProfesseurId(professeur1.getId()); // Même professeur que cours1 -> conflit
        dto.setEtudiantIds(Arrays.asList(etudiant2.getId())); 
        dto.setTypeCours("DISTANCIEL"); // Distanciel pour éviter un conflit de salle
        dto.setLienReunion("https://test-meeting.com/conflict");

        // Conversion du DTO en JSON
        String dtoJson = objectMapper.writeValueAsString(dto);

        // Envoi de la requête POST et vérification que le code de retour est 409 CONFLICT
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/cours/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
                
        // Vérifions simplement que la réponse contient une description du conflit
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.length() > 0, "La réponse ne doit pas être vide");
        // La réponse peut contenir soit message, soit conflits selon l'implémentation
        assertTrue(responseContent.contains("conflit") || responseContent.contains("Conflits") ||
                responseContent.contains("PROFESSEUR"), "La réponse doit mentionner un conflit");
    }
}
