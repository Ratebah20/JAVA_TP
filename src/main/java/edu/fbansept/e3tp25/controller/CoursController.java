package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.CoursDao;
import edu.fbansept.e3tp25.model.Cours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cours")
public class CoursController {

    protected CoursDao coursDao;

    @Autowired
    public CoursController(CoursDao coursDao) {
        this.coursDao = coursDao;
    }

    @GetMapping("/liste")
    public ResponseEntity<List<Cours>> getAll() {
        return ResponseEntity.ok(coursDao.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cours> getById(@PathVariable int id) {

        Optional<Cours> coursOptional = coursDao.findById(id);

        if (coursOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(coursOptional.get());
    }

    @PostMapping("")
    public ResponseEntity<Cours> add(@RequestBody Cours cours) {
        coursDao.save(cours);
        return new ResponseEntity<>(cours, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cours> update(
            @PathVariable int id,
            @RequestBody Cours coursEnvoye) {

        coursEnvoye.setId(id);

        Optional<Cours> coursBaseDeDonneesOptional = coursDao.findById(id);

        if (coursBaseDeDonneesOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        coursDao.save(coursEnvoye);

        return new ResponseEntity<>(coursEnvoye, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Cours> delete(@PathVariable int id) {

        Optional<Cours> coursBaseDeDonneesOptional = coursDao.findById(id);

        if (coursBaseDeDonneesOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        coursDao.deleteById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
