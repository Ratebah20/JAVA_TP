package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.CoursDao;
import edu.fbansept.e3tp25.dto.CreateCoursDto;
import edu.fbansept.e3tp25.model.Cours;
import edu.fbansept.e3tp25.model.Distanciel;
import edu.fbansept.e3tp25.model.Presentiel;
import edu.fbansept.e3tp25.service.CoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cours")
@CrossOrigin
public class CoursController {

    protected CoursDao coursDao;
    protected CoursService coursService;

    @Autowired
    public CoursController(CoursDao coursDao, CoursService coursService) {
        this.coursDao = coursDao;
        this.coursService = coursService;
    }

    @GetMapping("/liste")
    public ResponseEntity<List<Cours>> getAll() {
        return ResponseEntity.ok(coursDao.findAll());
    }
    
    @GetMapping("/presentiel/liste")
    public ResponseEntity<List<Presentiel>> getAllPresentiel() {
        return ResponseEntity.ok(coursDao.findAll().stream()
            .filter(cours -> cours instanceof Presentiel)
            .map(cours -> (Presentiel) cours)
            .toList());
    }
    
    @GetMapping("/distanciel/liste")
    public ResponseEntity<List<Distanciel>> getAllDistanciel() {
        return ResponseEntity.ok(coursDao.findAll().stream()
            .filter(cours -> cours instanceof Distanciel)
            .map(cours -> (Distanciel) cours)
            .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cours> getById(@PathVariable int id) {

        Optional<Cours> coursOptional = coursDao.findById(id);

        if (coursOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(coursOptional.get());
    }

    // Ancienne méthode conservée pour compatibilité
    @PostMapping("")
    public ResponseEntity<Cours> add(@RequestBody Cours cours) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/creer")
    public ResponseEntity<Cours> creerCours(@RequestBody CreateCoursDto dto) {
        Cours cours = coursService.creerCours(dto);
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
