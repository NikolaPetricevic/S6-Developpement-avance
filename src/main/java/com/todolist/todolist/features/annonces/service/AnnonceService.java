package com.todolist.todolist.features.annonces.service;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;

import java.util.List;

public interface AnnonceService {

    public List<AnnonceDTO> findAll();

    public AnnonceDTO findOne(Long id);

    public AnnonceDTO create(AnnonceDTO annonceDTO);

    public AnnonceDTO update(Long id, AnnonceDTO annonceDTO);

    public void delete(Long id);

}
