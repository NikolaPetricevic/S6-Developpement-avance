package com.todolist.todolist.features.annonces.controller;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.service.AnnonceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/annonces")
@RequiredArgsConstructor
@Tag(name = "Annonces")
public class AnnonceController {

    private final AnnonceService annonceService;

    @GetMapping
    public ResponseEntity<List<AnnonceDTO>> findAll() {
        return ResponseEntity.ok(annonceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnonceDTO> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(annonceService.findOne(id));
    }

    @PostMapping
    public ResponseEntity<AnnonceDTO> create(@RequestBody @Valid AnnonceDTO annonceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(annonceService.create(annonceDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnonceDTO> update(@PathVariable Long id, @RequestBody @Valid AnnonceDTO annonceDTO) {
        return ResponseEntity.ok(annonceService.update(id, annonceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        annonceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}