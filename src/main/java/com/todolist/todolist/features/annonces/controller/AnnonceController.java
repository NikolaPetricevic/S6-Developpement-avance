package com.todolist.todolist.features.annonces.controller;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.dto.AnnonceFilterDTO;
import com.todolist.todolist.features.annonces.service.AnnonceService;
import com.todolist.todolist.features.annonces.utils.AnnonceSortValidator;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/annonces")
@RequiredArgsConstructor
@Tag(name = "Annonces")
@SecurityRequirement(name = "bearerAuth")
public class AnnonceController {

    private final AnnonceService annonceService;
    private final AnnonceSortValidator annonceSortValidator;

    @GetMapping
    public ResponseEntity<Page<AnnonceDTO>> findAll(
            @ModelAttribute AnnonceFilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        annonceSortValidator.validate(sortBy);

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(annonceService.findAll(filter, pageable));
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