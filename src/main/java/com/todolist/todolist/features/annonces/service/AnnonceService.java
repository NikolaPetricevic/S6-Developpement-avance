package com.todolist.todolist.features.annonces.service;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.dto.AnnonceFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnonceService {

    Page<AnnonceDTO> findAll(AnnonceFilterDTO filter, Pageable pageable);

    public AnnonceDTO findOne(Long id);

    public AnnonceDTO create(AnnonceDTO annonceDTO);

    public AnnonceDTO update(Long id, AnnonceDTO annonceDTO);

    public void delete(Long id);

}
