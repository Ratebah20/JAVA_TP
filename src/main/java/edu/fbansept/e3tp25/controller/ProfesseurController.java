package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.ProfesseurDao;
import edu.fbansept.e3tp25.model.Professeur;
import jakarta.persistence.DiscriminatorColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professeur")
public class ProfesseurController {

    protected ProfesseurDao professeurDao;

    @Autowired
    public ProfesseurController(ProfesseurDao professeurDao) {
        this.professeurDao = professeurDao;
    }

    @GetMapping("/liste")
    public ResponseEntity<List<Professeur>> getAll() {
        return ResponseEntity.ok(professeurDao.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professeur> getById(@PathVariable int id) {

        Optional<Professeur> professeurOptional = professeurDao.findById(id);

        if (professeurOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(professeurOptional.get());
    }

    @PostMapping("")
    public ResponseEntity<Professeur> add(@RequestBody Professeur professeur) {
        professeurDao.save(professeur);
        return new ResponseEntity<>(professeur, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Professeur> update(
            @PathVariable int id,
            @RequestBody Professeur professeurEnvoye) {

        professeurEnvoye.setId(id);

        Optional<Professeur> professeurBaseDeDonneesOptional = professeurDao.findById(id);

        if (professeurBaseDeDonneesOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        //on empêche la modification du mot de passe et de l'email
        professeurEnvoye.setEmail(professeurBaseDeDonneesOptional.get().getEmail());
        professeurEnvoye.setPassword(professeurBaseDeDonneesOptional.get().getPassword());

        professeurDao.save(professeurEnvoye);

        return new ResponseEntity<>(professeurEnvoye, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Professeur> delete(@PathVariable int id) {

        Optional<Professeur> professeurBaseDeDonneesOptional = professeurDao.findById(id);

        if (professeurBaseDeDonneesOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        professeurDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
