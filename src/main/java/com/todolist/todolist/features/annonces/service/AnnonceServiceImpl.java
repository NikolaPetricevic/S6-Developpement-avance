package com.todolist.todolist.features.annonces.service;

import com.todolist.todolist.exceptions.ForbiddenException;
import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.dto.AnnonceFilterDTO;
import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.exceptions.AnnonceNotEditableException;
import com.todolist.todolist.features.annonces.exceptions.AnnonceNotFoundException;
import com.todolist.todolist.features.annonces.mapper.AnnonceMapper;
import com.todolist.todolist.features.annonces.repository.AnnonceRepository;
import com.todolist.todolist.features.annonces.utils.AnnonceSpecifications;
import com.todolist.todolist.features.categories.entity.CategoryEntity;
import com.todolist.todolist.features.categories.repository.CategoryRepository;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.features.users.enums.RoleEnum;
import com.todolist.todolist.features.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.todolist.todolist.features.annonces.exceptions.AuthorNotFoundException;
import com.todolist.todolist.features.categories.exceptions.CategoryNotFoundException;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AnnonceServiceImpl implements AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final AnnonceMapper annonceMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<AnnonceDTO> findAll(AnnonceFilterDTO filter, Pageable pageable) {
        Specification<AnnonceEntity> spec = Specification.unrestricted();

        if (StringUtils.hasText(filter.getKeyword())) {
            spec = spec.and(AnnonceSpecifications.hasKeyword(filter.getKeyword()));
        }
        if (filter.getStatus() != null) {
            spec = spec.and(AnnonceSpecifications.hasStatus(filter.getStatus()));
        }
        if (filter.getCategoryId() != null) {
            spec = spec.and(AnnonceSpecifications.hasCategoryId(filter.getCategoryId()));
        }
        if (filter.getAuthorId() != null) {
            spec = spec.and(AnnonceSpecifications.hasAuthorId(filter.getAuthorId()));
        }
        if (filter.getFromDate() != null) {
            spec = spec.and(AnnonceSpecifications.fromDate(filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            spec = spec.and(AnnonceSpecifications.toDate(filter.getToDate()));
        }

        return annonceRepository.findAll(spec, pageable).map(annonceMapper::toDTO);
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        if (!annonce.getAuthor().getId().equals(currentUser.getId()) && currentUser.getRole() != RoleEnum.ROLE_ADMIN) {
            throw new ForbiddenException("You are not the author of this annonce.");
        }

        if ((annonce.getStatus() == StatusEnum.PUBLISHED || annonce.getStatus() == StatusEnum.ARCHIVED)
                && currentUser.getRole() != RoleEnum.ROLE_ADMIN) {
            throw new AnnonceNotEditableException();
        }

        if (annonceDTO.getStatus() == StatusEnum.ARCHIVED && currentUser.getRole() != RoleEnum.ROLE_ADMIN) {
            throw new ForbiddenException("Only administrators can archive annonces.");
        }

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
        annonceRepository.findById(id).orElseThrow(AnnonceNotFoundException::new);
        annonceRepository.deleteById(id);
    }
}