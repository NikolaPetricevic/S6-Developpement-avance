package com.todolist.todolist.features.annonces.service;

import com.todolist.todolist.exceptions.ResourceNotFoundException;
import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.exceptions.NotArchivedException;
import com.todolist.todolist.features.annonces.mapper.AnnonceMapper;
import com.todolist.todolist.features.annonces.entity.Annonce;
import com.todolist.todolist.features.annonces.repository.AnnonceRepository;
import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.features.categories.entity.Category;
import com.todolist.todolist.features.categories.repository.CategoryRepository;
import com.todolist.todolist.features.users.entity.User;
import com.todolist.todolist.features.users.repository.UserRepository;
import com.todolist.todolist.utils.PaginatedResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;


import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public AnnonceService() {
        this.annonceRepository = new AnnonceRepository();
        this.entityManagerFactory = JPAUtil.getEntityManagerFactory();
        this.userRepository = new UserRepository();
        this.categoryRepository = new CategoryRepository();
    }

    public PaginatedResponse<AnnonceDTO> searchAnnonces(String keyword, Long categoryId,
                                                        StatusEnum status, int page, int size) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            log.info("Searching annonces, keyword : {}, categoryId : {}, status : {}, page : {}, size : {}", keyword, categoryId, status, page, size);
            List<Annonce> annonces = annonceRepository.searchWithFilters(em, keyword, categoryId, status, page, size);
            long totalElements = annonceRepository.countWithFilters(em, keyword, categoryId, status);
            int totalPages = (int) Math.ceil((double) totalElements / size);

            log.info("Searching annonces, totalElements : {}, totalPages : {}", totalElements, totalPages);
            List<AnnonceDTO> annonceDTOs = annonces.stream()
                    .map(AnnonceMapper::toDTO)
                    .collect(Collectors.toList());

            return new PaginatedResponse<>(annonceDTOs, page, size, totalElements, totalPages);
        } finally {
            em.close();
        }
    }

    public AnnonceDTO findOne(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            log.info("Searching annonce of id {}", id);
            Annonce annonce = annonceRepository.findOne(em, id);

            if (annonce == null) {
                log.warn("Annonce of id {} not found", id);
                throw new ResourceNotFoundException("Annonce", id);
            }

            log.info("Annonce of id {} found", id);
            log.info("Annonce : {}", annonce);

            return AnnonceMapper.toDTO(annonce);
        } finally {
            em.close();
        }
    }

    public AnnonceDTO createAnnonce(AnnonceDTO annonceDTO) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        log.info("Beginning transaction");

        try {
            Annonce annonce = AnnonceMapper.toEntity(annonceDTO);

            annonce.setDate(new Timestamp(System.currentTimeMillis()));

            if (annonceDTO.getAuthor_id() != null) {
                User author = userRepository.findOne(annonceDTO.getAuthor_id());
                if (author == null) {
                    log.warn("User of author_id {} not found", annonceDTO.getAuthor_id());
                    throw new ResourceNotFoundException("User", annonceDTO.getAuthor_id());
                }
                annonce.setAuthor(author);
            }

            if (annonceDTO.getCategory_id() != null) {
                Category category = categoryRepository.findOne(annonceDTO.getCategory_id());
                if (category == null) {
                    log.warn("Category of category_id {} not found", annonceDTO.getCategory_id());
                    throw new ResourceNotFoundException("Category", annonceDTO.getCategory_id());
                }
                annonce.setCategory(category);
            }

            Annonce createdAnnonce = annonceRepository.create(em, annonce);

            transaction.commit();

            log.info("Created Annonce {}", createdAnnonce);
            return AnnonceMapper.toDTO(createdAnnonce);

        } catch (Exception e) {
            transaction.rollback();
            log.info("Rolling back transaction");
            throw e;
        } finally {
            em.close();
        }
    }

    public AnnonceDTO updateAnnonce(Long id, AnnonceDTO annonceDTO, Long currentUserId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        log.info("Beginning transaction");

        try {
            Annonce existingAnnonce = annonceRepository.findOne(em, id);
            if (existingAnnonce == null) {
                log.warn("Annonce of id {} not found", id);
                throw new ResourceNotFoundException("Annonce", id);
            }

            if (!existingAnnonce.getAuthor().getId().equals(currentUserId)) {
                log.warn("User of id {} not authorized to update annonce", currentUserId);
                throw new ForbiddenException("You are not allowed to update this annonce");
            }

            // Permet de n'autoriser la modification seulement si l'annonce est en DRAFT
            if(existingAnnonce.getStatus() == StatusEnum.DRAFT) {
                log.info("Annonce status is DRAFT, allowing update");

                existingAnnonce.setTitle(annonceDTO.getTitle());
                existingAnnonce.setDescription(annonceDTO.getDescription());
                existingAnnonce.setAdress(annonceDTO.getAdress());
                existingAnnonce.setMail(annonceDTO.getMail());

                if (annonceDTO.getAuthor_id() != null) {
                    User author = userRepository.findOne(annonceDTO.getAuthor_id());
                    if (author == null) {
                        log.warn("User of author_id {} not found", annonceDTO.getAuthor_id());
                        throw new ResourceNotFoundException("User", annonceDTO.getAuthor_id());
                    }
                    existingAnnonce.setAuthor(author);
                }

                if (annonceDTO.getCategory_id() != null) {
                    Category category = categoryRepository.findOne(annonceDTO.getCategory_id());
                    if (category == null) {
                        log.warn("Category of category_id {} not found", annonceDTO.getCategory_id());
                        throw new ResourceNotFoundException("Category", annonceDTO.getCategory_id());
                    }
                    existingAnnonce.setCategory(category);
                }

            }

            // On autorise la modification du statut dans tous les cas
            existingAnnonce.setStatus(annonceDTO.getStatus());

            Annonce updatedAnnonce = annonceRepository.update(em, existingAnnonce);

            transaction.commit();
            log.info("Updated Annonce {}", updatedAnnonce);

            return AnnonceMapper.toDTO(updatedAnnonce);

        } catch (Exception e) {
            transaction.rollback();
            log.info("Rolling back transaction");
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteAnnonce(Long id, Long currentUserId) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        log.info("Beginning transaction");

        try {
            Annonce existingAnnonce = annonceRepository.findOne(em, id);
            if (existingAnnonce == null) {
                log.warn("Annonce of id {} not found", id);
                throw new ResourceNotFoundException("Annonce", id);
            }

            if (!existingAnnonce.getAuthor().getId().equals(currentUserId)) {
                log.warn("User of id {} not authorized to delete annonce", currentUserId);
                throw new ForbiddenException("You are not allowed to delete this annonce");
            }

            if (existingAnnonce.getStatus() != StatusEnum.ARCHIVED) {
                log.warn("Annonce of id {} not archived. Delete not allowed", id);
                throw new NotArchivedException("The annonce is not archived.");
            }

            annonceRepository.delete(em, id);
            transaction.commit();
            log.info("Deleted Annonce {}", existingAnnonce);

        } catch (Exception e) {
            transaction.rollback();
            log.info("Rolling back transaction");
            throw e;
        } finally {
            em.close();
        }
    }
}
