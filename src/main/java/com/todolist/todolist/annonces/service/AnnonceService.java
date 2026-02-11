package com.todolist.todolist.annonces.service;

import com.todolist.todolist.annonces.utils.AnnonceSearchCriteria;
import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.annonces.repository.AnnonceRepository;
import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.annonces.enums.StatusEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final EntityManagerFactory entityManagerFactory;

    // Constructeur par défaut pour la production
    public AnnonceService() {
        this.annonceRepository = new AnnonceRepository();
        this.entityManagerFactory = JPAUtil.getEntityManagerFactory();
    }

    // Constructeur pour les tests (injection de dépendances)
    public AnnonceService(EntityManagerFactory entityManagerFactory) {
        this.annonceRepository = new AnnonceRepository();
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<Annonce> findByCriteria(AnnonceSearchCriteria criteria, int page, int size) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return annonceRepository.findByCriteria(em, criteria, page, size);
        } finally {
            em.close();
        }
    }

    public long countByCriteria(AnnonceSearchCriteria criteria) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return annonceRepository.countByCriteria(em, criteria);
        } finally {
            em.close();
        }
    }

    public Annonce findOne(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return annonceRepository.findOne(em, id);
        } finally {
            em.close();
        }
    }

    public Annonce createAnnonce(Annonce annonce) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            Annonce createdAnnonce = annonceRepository.create(em, annonce);
            transaction.commit();
            return createdAnnonce;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Annonce updateAnnonce(Annonce annonce) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            Annonce updatedAnnonce = annonceRepository.update(em, annonce);
            transaction.commit();
            return updatedAnnonce;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteAnnonce(Long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        try {
            annonceRepository.delete(em, id);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Annonce publish(Annonce annonce) {
        annonce.setStatus(StatusEnum.PUBLISHED);
        return this.updateAnnonce(annonce);
    }

    public Annonce archive(Annonce annonce) {
        annonce.setStatus(StatusEnum.ARCHIVED);
        return this.updateAnnonce(annonce);
    }
}
