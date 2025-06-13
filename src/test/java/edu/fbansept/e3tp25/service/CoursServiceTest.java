package edu.fbansept.e3tp25.service;

import edu.fbansept.e3tp25.dao.CoursDao;
import edu.fbansept.e3tp25.dao.EtudiantDao;
import edu.fbansept.e3tp25.dao.ProfesseurDao;
import edu.fbansept.e3tp25.dao.SalleDao;
import edu.fbansept.e3tp25.dto.CreateCoursDto;
import edu.fbansept.e3tp25.exception.CapaciteSalleException;
import edu.fbansept.e3tp25.exception.ConflitHoraireException;
import edu.fbansept.e3tp25.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoursServiceTest {

    @Mock
    private CoursDao coursDao;

    @Mock
    private EtudiantDao etudiantDao;

    @Mock
    private ProfesseurDao professeurDao;

    @Mock
    private SalleDao salleDao;

    @InjectMocks
    private CoursService coursService;

    private Professeur professeur;
    private Etudiant etudiant;
    private Salle salle;
    private CreateCoursDto coursDto;

    @BeforeEach
    void setUp() {
        // Initialiser les entités pour les tests
        professeur = new Professeur();
        professeur.setId(1);
        professeur.setEmail("prof@test.com");

        etudiant = new Etudiant();
        etudiant.setId(1);
        etudiant.setEmail("etudiant@test.com");

        salle = new Salle();
        salle.setId(1);
        salle.setNom("A101");
        salle.setCapacite(30);

        // Créer un DTO de test
        coursDto = new CreateCoursDto();
        coursDto.setNom("Test Cours");
        coursDto.setDebut(LocalDateTime.now().plusDays(1));
        coursDto.setDuree(90);
        coursDto.setProfesseurId(1);
        coursDto.setEtudiantIds(List.of(1));
        coursDto.setTypeCours("PRESENTIEL");
        coursDto.setSalleId(1);
    }

    @Test
    void testCreerCoursPresentiel() {
        // Configurer les mocks avec uniquement les appels nécessaires
        when(professeurDao.findById(1)).thenReturn(Optional.of(professeur));
        when(etudiantDao.findAllById(List.of(1))).thenReturn(List.of(etudiant));
        when(salleDao.findById(1)).thenReturn(Optional.of(salle));
        
        // Vérifier l'ordre d'exécution dans CoursService pour s'assurer que ces appels sont bien nécessaires
        lenient().when(coursDao.findConflictsProfesseur(any(), any(), any())).thenReturn(List.of());
        lenient().when(coursDao.findConflictsEtudiant(any(), any(), any())).thenReturn(List.of());
        lenient().when(coursDao.isSalleOccupee(any(), any(), any())).thenReturn(false);
        
        when(coursDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécuter le test
        Cours cours = coursService.creerCours(coursDto);

        // Vérifier le résultat
        assertTrue(cours instanceof Presentiel);
        assertEquals("Test Cours", cours.getNom());
        assertEquals(90, cours.getDuree());
        assertEquals(professeur, cours.getProfesseur());
        assertEquals(1, cours.getEtudiantList().size());
        assertEquals(etudiant, cours.getEtudiantList().get(0));
        assertEquals(salle, ((Presentiel) cours).getSalle());
    }

    @Test
    void testCreerCoursDistanciel() {
        // Configurer le DTO
        coursDto.setTypeCours("DISTANCIEL");
        coursDto.setSalleId(null);
        coursDto.setLienReunion("https://meet.example.com/abc123");

        // Configurer les mocks
        when(professeurDao.findById(1)).thenReturn(Optional.of(professeur));
        when(etudiantDao.findAllById(List.of(1))).thenReturn(List.of(etudiant));
        when(coursDao.findConflictsProfesseur(any(), any(), any())).thenReturn(List.of());
        when(coursDao.findConflictsEtudiant(any(), any(), any())).thenReturn(List.of());
        when(coursDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Exécuter le test
        Cours cours = coursService.creerCours(coursDto);

        // Vérifier le résultat
        assertTrue(cours instanceof Distanciel);
        assertEquals("Test Cours", cours.getNom());
        assertEquals(coursDto.getLienReunion(), ((Distanciel) cours).getLienReunion());
    }

    @Test
    void testConflitProfesseur() {
        // Modification du coursDto pour utiliser une date spécifique pour le test
        LocalDateTime dateTest = LocalDateTime.of(2025, 7, 15, 10, 0); // 10h00
        coursDto.setDebut(dateTest);
        coursDto.setDuree(60); // 1 heure
        
        // Configurer un conflit pour le professeur avec un chevauchement
        List<Cours> conflits = new ArrayList<>();
        Cours existingCours = new Presentiel();
        existingCours.setNom("Cours existant");
        existingCours.setDebut(dateTest.minusMinutes(30)); // 9h30
        existingCours.setDuree(120);  // 2 heures -> fin à 11h30, donc chevauche
        conflits.add(existingCours);
        
        // Configurer tous les mocks nécessaires
        when(professeurDao.findById(1)).thenReturn(Optional.of(professeur));
        lenient().when(etudiantDao.findAllById(List.of(1))).thenReturn(List.of(etudiant));
        lenient().when(salleDao.findById(1)).thenReturn(Optional.of(salle));
        when(coursDao.findConflictsProfesseur(any(), any(), any())).thenReturn(conflits);
        
        // Comme on veut tester uniquement le conflit professeur, on désactive les autres conflits
        lenient().when(coursDao.findConflictsEtudiant(any(), any(), any())).thenReturn(List.of());
        lenient().when(coursDao.isSalleOccupee(any(), any(), any())).thenReturn(false);

        // Vérifier que l'exception est lancée
        assertThrows(ConflitHoraireException.class, () -> coursService.creerCours(coursDto));

        // Vérifier que le cours n'est pas sauvegardé
        verify(coursDao, never()).save(any());
    }

    @Test
    void testCapaciteSalleDepassee() {
        // Modifier la capacité de la salle
        salle.setCapacite(0); // Capacité insuffisante

        when(professeurDao.findById(1)).thenReturn(Optional.of(professeur));
        when(etudiantDao.findAllById(List.of(1))).thenReturn(List.of(etudiant));
        when(salleDao.findById(1)).thenReturn(Optional.of(salle));
        
        // Ces mock ne sont pas nécessaires pour ce test car l'exception de capacité est lancée avant
        // when(coursDao.findConflictsProfesseur(any(), any(), any())).thenReturn(List.of());
        // when(coursDao.findConflictsEtudiant(any(), any(), any())).thenReturn(List.of());
        // when(coursDao.isSalleOccupee(any(), any(), any())).thenReturn(false);

        // Vérifier que l'exception est lancée
        assertThrows(CapaciteSalleException.class, () -> coursService.creerCours(coursDto));

        // Vérifier que le cours n'est pas sauvegardé
        verify(coursDao, never()).save(any());
    }
}
