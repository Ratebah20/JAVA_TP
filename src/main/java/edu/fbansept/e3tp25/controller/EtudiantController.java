package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.EtudiantDao;
import edu.fbansept.e3tp25.model.Etudiant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiant")
public class EtudiantController {

    protected EtudiantDao etudiantDao;

    @Autowired
    public EtudiantController(EtudiantDao etudiantDao) {
        this.etudiantDao = etudiantDao;
    }

    @GetMapping("/liste")
    public List<Etudiant> getAll() {
        return etudiantDao.findAll();
    }

    @PostMapping("")
    public ResponseEntity<Etudiant> add(@RequestBody Etudiant etudiant) {
        etudiantDao.save(etudiant);
        return new ResponseEntity<>(etudiant, HttpStatus.CREATED);
    }

}
