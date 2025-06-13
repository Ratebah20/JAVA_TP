package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.EtudiantDao;
import edu.fbansept.e3tp25.model.Etudiant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/etudiant")
public class EtudiantController {

    protected EtudiantDao etudiantDao;

    @Autowired
    public EtudiantController(EtudiantDao etudiantDao) {
        this.etudiantDao = etudiantDao;
    }

    @GetMapping("/liste")
    public ResponseEntity<List<Etudiant>> getAll() {
        return ResponseEntity.ok(etudiantDao.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getById(@PathVariable int id) {

        Optional<Etudiant> etudiantOptional = etudiantDao.findById(id);

        if (etudiantOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(etudiantOptional.get());
    }

    @PostMapping("")
    public ResponseEntity<Etudiant> add(@RequestBody Etudiant etudiant) {
        etudiantDao.save(etudiant);
        return new ResponseEntity<>(etudiant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> update(
            @PathVariable int id,
            @RequestBody Etudiant etudiantEnvoye) {

        etudiantEnvoye.setId(id);

        Optional<Etudiant> etudiantBaseDeDonneesOptional = etudiantDao.findById(id);

        if (etudiantBaseDeDonneesOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //on empêche la modification du mot de passe et de l'email
        etudiantEnvoye.setEmail(etudiantBaseDeDonneesOptional.get().getEmail());
        etudiantEnvoye.setPassword(etudiantBaseDeDonneesOptional.get().getPassword());

        etudiantDao.save(etudiantEnvoye);

        return new ResponseEntity<>(etudiantEnvoye, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Etudiant> delete(@PathVariable int id) {

        Optional<Etudiant> etudiantBaseDeDonneesOptional = etudiantDao.findById(id);

        if (etudiantBaseDeDonneesOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        etudiantDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
