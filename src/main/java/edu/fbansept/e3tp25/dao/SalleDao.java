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
           "p.debut < :fin AND :debut < p.getFin())")
    List<Salle> findSallesDisponibles(@Param("debut") LocalDateTime debut, 
                                      @Param("fin") LocalDateTime fin);
}
