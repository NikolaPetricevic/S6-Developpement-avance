package com.todolist.todolist.features.annonces.repository;

import com.todolist.todolist.features.annonces.entity.Annonce;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AnnonceRepository {

    public List<Annonce> searchWithFilters(EntityManager em, String keyword, Long categoryId,
                                           StatusEnum status, int page, int size) {
        StringBuilder queryStr = new StringBuilder("SELECT a FROM Annonce a WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            queryStr.append(" AND (LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword))");
        }

        if (categoryId != null) {
            queryStr.append(" AND a.category.id = :categoryId");
        }

        if (status != null) {
            queryStr.append(" AND a.status = :status");
        }

        queryStr.append(" ORDER BY a.date DESC");

        TypedQuery<Annonce> query = em.createQuery(queryStr.toString(), Annonce.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        return query
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countWithFilters(EntityManager em, String keyword, Long categoryId, StatusEnum status) {
        StringBuilder queryStr = new StringBuilder("SELECT COUNT(a) FROM Annonce a WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            queryStr.append(" AND (LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword))");
        }

        if (categoryId != null) {
            queryStr.append(" AND a.category.id = :categoryId");
        }

        if (status != null) {
            queryStr.append(" AND a.status = :status");
        }

        TypedQuery<Long> query = em.createQuery(queryStr.toString(), Long.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getSingleResult();
    }

    public long count(EntityManager em) {
        return em.createQuery("SELECT COUNT(a) FROM Annonce a", Long.class)
                .getSingleResult();
    }

    public Annonce findOne(EntityManager em, Long id) {
        return em.find(Annonce.class, id);
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
