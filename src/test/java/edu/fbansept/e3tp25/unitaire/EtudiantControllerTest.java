package edu.fbansept.e3tp25.unitaire;

import edu.fbansept.e3tp25.controller.EtudiantController;
import edu.fbansept.e3tp25.mock.EtudiantDaoMock;
import edu.fbansept.e3tp25.model.Etudiant;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class EtudiantControllerTest {

    protected EtudiantController etudiantController;

    @BeforeEach
    public void init() {
        etudiantController = new EtudiantController(new EtudiantDaoMock());
    }

    @Test
    public void recupererTousLesUtilisateurs_retourneReponseOk() {
        assertEquals(
                HttpStatus.OK,
                etudiantController.getAll().getStatusCode());
    }

    @Test
    public void ajouterUnUtilisateur_retourneReponseCreated() {
        assertEquals(
                HttpStatus.CREATED,
                etudiantController.add(new Etudiant()).getStatusCode());
    }
}
