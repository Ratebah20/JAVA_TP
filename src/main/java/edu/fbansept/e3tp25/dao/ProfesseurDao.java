package edu.fbansept.e3tp25.dao;

import edu.fbansept.e3tp25.model.Etudiant;
import edu.fbansept.e3tp25.model.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesseurDao extends JpaRepository<Professeur, Integer> {
}
