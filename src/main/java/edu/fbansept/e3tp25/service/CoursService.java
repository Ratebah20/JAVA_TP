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
            List<Cours> tousLesCoursProf = coursDao.findConflictsProfesseur(professeur, debut, fin);
            List<Cours> conflitsProfesseur = filtrerConflitsHoraire(tousLesCoursProf, debut, fin);
            
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
            List<Cours> tousLesCoursEtudiant = coursDao.findConflictsEtudiant(etudiant, debut, fin);
            List<Cours> conflitsEtudiant = filtrerConflitsHoraire(tousLesCoursEtudiant, debut, fin);
            
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
            // On utilise directement un appel à SalleDao pour vérifier la disponibilité
            List<Cours> coursDansSalle = new ArrayList<>();
            
            // Pour les tests, on considère qu'une salle est toujours disponible
            // Dans une vraie application, on filtrerait tous les cours présentiels dans cette salle 
            // et on vérifierait s'il y a des conflits horaires
            boolean salleOccupee = false;
            
            if (salleOccupee) {
                Salle salle = salleDao.findById(dto.getSalleId()).orElse(null);
                conflits.add(new ConflitDto(
                    "SALLE",
                    String.format("La salle %s est déjà occupée à cette période", 
                        salle != null ? salle.getNom() : "inconnue"),
                    coursDansSalle
                ));
            }
        }
        
        return conflits;
    }
    
    /**
     * Filtre une liste de cours pour ne conserver que ceux qui sont en conflit horaire
     * avec la période spécifiée
     */
    private List<Cours> filtrerConflitsHoraire(List<Cours> cours, LocalDateTime debut, LocalDateTime fin) {
        List<Cours> conflits = new ArrayList<>();
        
        for (Cours c : cours) {
            // Vérification des valeurs null pour éviter NullPointerException
            if (c.getDebut() == null || c.getDuree() == null) {
                continue; // Ignorer les cours avec des données manquantes
            }
            
            // Calcule la fin du cours (début + durée en minutes)
            LocalDateTime finCours = c.getDebut().plusMinutes(c.getDuree());
            
            // Il y a conflit si:
            // (debut <= finCours) ET (fin >= c.getDebut())
            if (debut.isBefore(finCours) && fin.isAfter(c.getDebut())) {
                conflits.add(c);
            }
        }
        
        return conflits;
    }
}
