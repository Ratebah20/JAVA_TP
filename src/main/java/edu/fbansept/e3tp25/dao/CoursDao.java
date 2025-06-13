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
    
    /**
     * Trouve les conflits pour un professeur donné entre deux dates
     */
    @Query("SELECT c FROM Cours c WHERE c.professeur = :professeur")
    List<Cours> findConflictsProfesseur(
            @Param("professeur") Professeur professeur,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
    
    /**
     * Trouve les cours qui sont en conflit avec la période spécifiée pour un étudiant donné
     */
    @Query("SELECT c FROM Cours c JOIN c.etudiantList e WHERE e = :etudiant")
    List<Cours> findConflictsEtudiant(
            @Param("etudiant") Etudiant etudiant,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
    
    /**
     * Vérifie si une salle est occupée pendant une période donnée
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Presentiel p WHERE p.salle.id = :salleId")
    boolean isSalleOccupee(
            @Param("salleId") Integer salleId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
}
