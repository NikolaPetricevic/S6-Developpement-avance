package com.todolist.todolist.features.annonces.service;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.annonces.exceptions.AnnonceNotFoundException;
import com.todolist.todolist.features.annonces.mapper.AnnonceMapper;
import com.todolist.todolist.features.annonces.repository.AnnonceRepository;
import com.todolist.todolist.features.categories.entity.CategoryEntity;
import com.todolist.todolist.features.categories.mapper.CategoryMapper;
import com.todolist.todolist.features.categories.repository.CategoryRepository;
import com.todolist.todolist.features.categories.service.CategoryService;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.features.users.mappers.UserMapper;
import com.todolist.todolist.features.users.repository.UserRepository;
import com.todolist.todolist.features.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.todolist.todolist.features.annonces.exceptions.AuthorNotFoundException;
import com.todolist.todolist.features.categories.exceptions.CategoryNotFoundException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnonceServiceImpl implements AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final AnnonceMapper annonceMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AnnonceDTO> findAll() {
        return annonceRepository.findAll()
                .stream()
                .map(annonceMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnnonceDTO findOne(Long id) {
        AnnonceEntity annonce = annonceRepository.findById(id)
                .orElseThrow(AnnonceNotFoundException::new);

        return annonceMapper.toDTO(annonce);
    }

    @Override
    @Transactional
    public AnnonceDTO create(AnnonceDTO annonceDTO) {
        UserEntity author = userRepository.findById(annonceDTO.getAuthor_id())
                .orElseThrow(AuthorNotFoundException::new);

        CategoryEntity category = categoryRepository.findById(annonceDTO.getCategory_id())
                .orElseThrow(CategoryNotFoundException::new);

        AnnonceEntity annonce = annonceMapper.toEntity(annonceDTO);
        annonce.setAuthor(author);
        annonce.setCategoryEntity(category);
        annonce.setDate(Timestamp.from(Instant.now()));

        return annonceMapper.toDTO(annonceRepository.save(annonce));
    }

    @Override
    @Transactional
    public AnnonceDTO update(Long id, AnnonceDTO annonceDTO) {
        AnnonceEntity annonce = annonceRepository.findById(id)
                .orElseThrow(AnnonceNotFoundException::new);

        annonce.setTitle(annonceDTO.getTitle());
        annonce.setDescription(annonceDTO.getDescription());
        annonce.setAdress(annonceDTO.getAdress());
        annonce.setMail(annonceDTO.getMail());
        annonce.setStatus(annonceDTO.getStatus());

        if (annonceDTO.getAuthor_id() != null) {
            userRepository.findById(annonceDTO.getAuthor_id()).orElseThrow(AuthorNotFoundException::new);
        }

        if (annonceDTO.getCategory_id() != null) {
            categoryRepository.findById(annonceDTO.getCategory_id()).orElseThrow(CategoryNotFoundException::new);
        }

        return annonceMapper.toDTO(annonceRepository.save(annonce));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!annonceRepository.existsById(id)) {
            throw new AnnonceNotFoundException();
        }

        annonceRepository.deleteById(id);
    }
}