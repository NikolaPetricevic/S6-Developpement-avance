package com.todolist.todolist.features.users.repository;

import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.features.users.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class UserRepository {

    public List<User> findAll(int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery("SELECT user FROM User user ORDER BY user.id", User.class)
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    public User findByUsername(String username) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findOne(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.find(User.class, id);
        }
    }

    public User create(User user) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            em.persist(user);
            return user;
        }
    }

    public User update(User user) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            return em.merge(user);
        }
    }

    public void delete(Long id) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
            }
        }
    }
}
