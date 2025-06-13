package edu.fbansept.e3tp25.dao;

import edu.fbansept.e3tp25.model.Administrateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrateurDao extends JpaRepository<Administrateur, Integer> {
}
