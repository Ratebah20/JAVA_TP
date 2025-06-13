package edu.fbansept.e3tp25.controller;

import edu.fbansept.e3tp25.dao.SalleDao;
import edu.fbansept.e3tp25.model.Salle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/salle")
@CrossOrigin
public class SalleController {
    
    @Autowired
    private SalleDao salleDao;
    
    @GetMapping("/liste")
    public List<Salle> liste() {
        return salleDao.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Salle> get(@PathVariable Integer id) {
        return salleDao.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("")
    public Salle create(@RequestBody Salle salle) {
        return salleDao.save(salle);
    }
    
    @GetMapping("/disponibles")
    public List<Salle> getSallesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(required = false) Integer capaciteMin) {
        
        List<Salle> sallesDisponibles = salleDao.findSallesDisponibles(debut, fin);
        
        if (capaciteMin != null) {
            return sallesDisponibles.stream()
                .filter(salle -> salle.getCapacite() >= capaciteMin)
                .toList();
        }
        
        return sallesDisponibles;
    }
}
