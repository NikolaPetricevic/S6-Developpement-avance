package com.todolist.todolist.features.annonces.mapper;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.Annonce;

public class AnnonceMapper {

    public static AnnonceDTO toDTO(Annonce annonce) {
        if (annonce == null) {
            return null;
        }

        return AnnonceDTO.builder()
                .id(annonce.getId())
                .title(annonce.getTitle())
                .description(annonce.getDescription())
                .adress(annonce.getAdress())
                .mail(annonce.getMail())
                .date(annonce.getDate())
                .status(annonce.getStatus())
                .author(annonce.getAuthor())
                .category(annonce.getCategory())
                .build();
    }

    public static Annonce toEntity(AnnonceDTO dto) {
        if (dto == null) {
            return null;
        }

        return Annonce.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .adress(dto.getAdress())
                .mail(dto.getMail())
                .date(dto.getDate())
                .status(dto.getStatus())
                .author(dto.getAuthor())
                .category(dto.getCategory())
                .build();
    }
}
