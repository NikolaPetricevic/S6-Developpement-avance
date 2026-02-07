package com.todolist.todolist.annonces.repository;

import com.todolist.todolist.annonces.utils.AnnonceSearchCriteria;
import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.database.JPAUtil;
import com.todolist.todolist.utils.QueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AnnonceRepository {

    //REMPLACE LE findAll() QU'ON AURAIT DE BASE POUR PERMETTRE D'UTILISER DES FILTRES SANS TROP ENCOMBRER LE CODE
    public List<Annonce> findByCriteria(AnnonceSearchCriteria criteria, int page, int size) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            QueryBuilder queryBuilder = buildQuery(criteria, false);

            TypedQuery<Annonce> query = em.createQuery(queryBuilder.getQuery(), Annonce.class);
            setParameters(query, queryBuilder.getParameters());

            return query
                    .setFirstResult(page * size)
                    .setMaxResults(size)
                    .getResultList();
        }
    }

    //PERMET DE COMPTER LE NOMBRE DE LIGNES PAR RAPPORT AUX FILTRES SAISIS
    public long countByCriteria(AnnonceSearchCriteria criteria) {
        try (EntityManager em = JPAUtil.getEntityManager()) {
            QueryBuilder queryBuilder = buildQuery(criteria, true);

            TypedQuery<Long> query = em.createQuery(queryBuilder.getQuery(), Long.class);
            setParameters(query, queryBuilder.getParameters());

            return query.getSingleResult();
        }
    }

    //BUILD LA QUERY SELON LES FILTRES SAISIS
    private QueryBuilder buildQuery(AnnonceSearchCriteria criteria, boolean isCount) {
        StringBuilder query = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();

        if (isCount) {
            query.append("SELECT COUNT(a) FROM Annonce a");
        } else {
            query.append("SELECT a FROM Annonce a");
        }

        StringBuilder whereClause = new StringBuilder();

        if (criteria != null) {
            if (criteria.hasKeyword()) {
                whereClause.append(" (LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword))");
                parameters.put("keyword", "%" + criteria.getKeyword().trim() + "%");
            }

            if (criteria.hasStatus()) {
                if (!whereClause.isEmpty()) {
                    whereClause.append(" AND");
                }
                whereClause.append(" a.status = :status");
                parameters.put("status", criteria.getStatus());
            }

            if (criteria.hasCategoryId()) {
                if (!whereClause.isEmpty()) {
                    whereClause.append(" AND");
                }
                whereClause.append(" a.category.id = :categoryId");
                parameters.put("categoryId", criteria.getCategoryId());
            }
        }

        if (!whereClause.isEmpty()) {
            query.append(" WHERE").append(whereClause);
        }

        if (!isCount) {
            query.append(" ORDER BY a.id");
        }

        return new QueryBuilder(query.toString(), parameters);
    }

    //AJOUTE LES PARAMETRES DE FILTRE A LA STRING DE LA REQUETE
    private void setParameters(TypedQuery<?> query, Map<String, Object> parameters) {
        parameters.forEach(query::setParameter);
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
