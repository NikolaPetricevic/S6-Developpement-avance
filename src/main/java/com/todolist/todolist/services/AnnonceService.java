package com.todolist.todolist.services;

import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.entities.Annonce;
import com.todolist.todolist.enums.StatusEnum;
import com.todolist.todolist.repositories.AnnonceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;

    public AnnonceService() {
        this.annonceRepository = new AnnonceRepository();
    }

    public List<Annonce> findAll(int page, int size) {
        return annonceRepository.findAll(page, size);
    }

    public List<Annonce> findByKeyword(String keyword, int page, int size) {
        return annonceRepository.findByKeyword(keyword, page, size);
    }

    public List<Annonce> findByCategory(Long categoryId, int page, int size) {
        return annonceRepository.findByCategory(categoryId, page, size);
    }

    public List<Annonce> findByStatus(StatusEnum status, int page, int size) {
        return annonceRepository.findByStatus(status, page, size);
    }

    public Annonce findOne(Long id) {
        return annonceRepository.findOne(id);
    }

    public Annonce createAnnonce(Annonce annonce) {
        EntityManager em = JPAUtil.getEntityManager();
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
        EntityManager em = JPAUtil.getEntityManager();
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
        EntityManager em = JPAUtil.getEntityManager();
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
