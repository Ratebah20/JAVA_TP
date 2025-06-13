package edu.fbansept.e3tp25.config;

import edu.fbansept.e3tp25.dao.*;
import edu.fbansept.e3tp25.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Initialise les données de test pour le profil "test"
 * Cette classe s'exécute automatiquement au démarrage de l'application
 * uniquement quand le profil "test" est activé
 */
@Component
@Profile("test")
public class TestDataInitializer implements ApplicationRunner {

    @Autowired
    private EtudiantDao etudiantDao;

    @Autowired
    private ProfesseurDao professeurDao;

    @Autowired
    private AdministrateurDao administrateurDao;

    @Autowired
    private SalleDao salleDao;

    @Autowired
    private CoursDao coursDao;

    @Override
    public void run(ApplicationArguments args) {
        // Création des utilisateurs
        Etudiant etudiant1 = new Etudiant();
        etudiant1.setEmail("etudiant1@test.com");
        etudiant1.setPassword("test123");
        etudiant1.setDateNaissance(LocalDate.of(2000, 1, 15));
        etudiantDao.save(etudiant1);

        Etudiant etudiant2 = new Etudiant();
        etudiant2.setEmail("etudiant2@test.com");
        etudiant2.setPassword("test123");
        etudiant2.setDateNaissance(LocalDate.of(2001, 3, 22));
        etudiantDao.save(etudiant2);

        Professeur prof1 = new Professeur();
        prof1.setEmail("professeur1@test.com");
        prof1.setPassword("test123");
        prof1.setAnneesExperience(5);
        professeurDao.save(prof1);

        Professeur prof2 = new Professeur();
        prof2.setEmail("professeur2@test.com");
        prof2.setPassword("test123");
        prof2.setAnneesExperience(10);
        professeurDao.save(prof2);

        Administrateur admin = new Administrateur();
        admin.setEmail("admin@test.com");
        admin.setPassword("admin123");
        administrateurDao.save(admin);

        // Création des salles
        Salle salleA = new Salle();
        salleA.setNom("Salle Test A");
        salleA.setCapacite(30);
        salleDao.save(salleA);

        Salle salleB = new Salle();
        salleB.setNom("Salle Test B");
        salleB.setCapacite(15);
        salleDao.save(salleB);

        Salle salleC = new Salle();
        salleC.setNom("Salle Test C");
        salleC.setCapacite(50);
        salleDao.save(salleC);

        // Création des cours
        // Cours présentiel 1
        Presentiel presentiel1 = new Presentiel();
        presentiel1.setNom("Cours Test 1");
        presentiel1.setDebut(LocalDateTime.of(2025, 7, 1, 9, 0));
        presentiel1.setDuree(90);
        presentiel1.setProfesseur(prof1);
        presentiel1.setEtudiantList(Arrays.asList(etudiant1, etudiant2));
        presentiel1.setSalle(salleA);
        coursDao.save(presentiel1);

        // Cours présentiel 2
        Presentiel presentiel2 = new Presentiel();
        presentiel2.setNom("Cours Test 2");
        presentiel2.setDebut(LocalDateTime.of(2025, 7, 1, 14, 0));
        presentiel2.setDuree(120);
        presentiel2.setProfesseur(prof2);
        presentiel2.setEtudiantList(Arrays.asList(etudiant1));
        presentiel2.setSalle(salleC);
        coursDao.save(presentiel2);

        // Cours distanciel
        Distanciel distanciel = new Distanciel();
        distanciel.setNom("Cours Test 3");
        distanciel.setDebut(LocalDateTime.of(2025, 7, 2, 10, 0));
        distanciel.setDuree(60);
        distanciel.setProfesseur(prof1);
        distanciel.setEtudiantList(Arrays.asList(etudiant2));
        distanciel.setLienReunion("https://reunion-test.com/abc123");
        coursDao.save(distanciel);
    }
}
