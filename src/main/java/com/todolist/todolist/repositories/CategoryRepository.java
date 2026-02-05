package com.todolist.todolist.repositories;

import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.entities.Category;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryRepository {

    public List<Category> findAll(int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT c FROM Category c ORDER BY c.id", Category.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public List<Category> findAll(int page) {
        return findAll(page, 20);
    }

    public Category findOne(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(Category.class, id);
        }
    }

    public Category create(Category category) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.persist(category);
            return category;
        }
    }

    public Category update(Category category) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.merge(category);
        }
    }

    public void delete(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
        }
    }

}
