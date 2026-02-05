package com.todolist.todolist.repositories;

import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.entities.Annonce;
import com.todolist.todolist.enums.StatusEnum;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnnonceRepository {

    public List<Annonce> findAll(int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT a FROM Annonce a ORDER BY a.id", Annonce.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> findByKeyword(String keyword, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Annonce a " +
                                    "WHERE LOWER(a.title) LIKE LOWER(:keyword) " +
                                    "OR LOWER(a.description) LIKE LOWER(:keyword) " +
                                    "ORDER BY a.id",
                            Annonce.class)
                    .setParameter("keyword", "%" + keyword + "%")
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> findByCategory(Long categoryId, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Annonce a " +
                                    "WHERE a.category.id = :categoryId " +
                                    "ORDER BY a.id",
                            Annonce.class)
                    .setParameter("categoryId", categoryId)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> findByStatus(StatusEnum status, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM Annonce a " +
                                    "WHERE a.status = :status " +
                                    "ORDER BY a.id",
                            Annonce.class)
                    .setParameter("status", status)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Annonce> findByKeyword(String keyword, int page) {
        return findByKeyword(keyword, page, 20);
    }

    public Annonce findOne(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(Annonce.class, id);
        }
    }

    public Annonce create(EntityManager em, Annonce annonce) {
        em.persist(annonce);
        return annonce;
    }

    public Annonce update(EntityManager em, Annonce annonce) {
        return em.merge(annonce);
    }

    public void delete(EntityManager em, Long id) {
        Annonce annonce = em.find(Annonce.class, id);
        if (annonce != null) {
            em.remove(annonce);
        }
    }
}
