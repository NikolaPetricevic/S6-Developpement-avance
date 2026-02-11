package com.todolist.todolist.annonces;

import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.annonces.repository.AnnonceRepository;
import com.todolist.todolist.categories.entity.Category;
import com.todolist.todolist.users.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnnonceRepository annonceRepository;

    @BeforeAll
    static void setUpAll() {
        emf = Persistence.createEntityManagerFactory("testUnit");
    }

    @AfterAll
    static void tearDownAll() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        annonceRepository = new AnnonceRepository();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            try {
                em.getTransaction().begin();
                // Supprimer dans l'ordre inverse des dépendances (FK)
                em.createQuery("DELETE FROM Annonce").executeUpdate();
                em.createQuery("DELETE FROM Category").executeUpdate();
                em.createQuery("DELETE FROM User").executeUpdate();
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } finally {
                em.close();
            }
        }
    }

    @Test
    void testCreate() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Category category = Category.builder()
                .label("Test Category")
                .build();

        Annonce annonce = Annonce.builder()
                .title("Test Annonce")
                .description("Description de test")
                .adress("123 Rue de Test")
                .mail("annonce@test.com")
                .date(new Timestamp(System.currentTimeMillis()))
                .author(user)
                .category(category)
                .build();

        em.getTransaction().begin();

        em.persist(user);
        em.persist(category);

        Annonce createdAnnonce = annonceRepository.create(em, annonce);

        em.getTransaction().commit();

        assertNotNull(createdAnnonce, "L'annonce créée ne devrait pas être null");
        assertNotNull(createdAnnonce.getId(), "L'ID de l'annonce devrait être généré");
        assertEquals("Test Annonce", createdAnnonce.getTitle(), "Le titre devrait correspondre");
        assertEquals("Description de test", createdAnnonce.getDescription(), "La description devrait correspondre");
        assertEquals("123 Rue de Test", createdAnnonce.getAdress(), "L'adresse devrait correspondre");
        assertEquals("annonce@test.com", createdAnnonce.getMail(), "L'email devrait correspondre");

        em.clear();
        Annonce annonceFromDb = em.find(Annonce.class, createdAnnonce.getId());
        assertNotNull(annonceFromDb, "L'annonce devrait être trouvée en base de données");
        assertEquals(createdAnnonce.getTitle(), annonceFromDb.getTitle(), "Le titre en base devrait correspondre");
    }

    @Test
    void testFindOne_WhenAnnonceExists() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Category category = Category.builder()
                .label("Test Category")
                .build();

        Annonce annonce = Annonce.builder()
                .title("Annonce à trouver")
                .description("Description de l'annonce")
                .adress("456 Avenue Test")
                .mail("find@test.com")
                .date(new Timestamp(System.currentTimeMillis()))
                .author(user)
                .category(category)
                .build();

        em.getTransaction().begin();
        em.persist(user);
        em.persist(category);
        em.persist(annonce);
        em.getTransaction().commit();

        Long annonceId = annonce.getId();

        Annonce foundAnnonce = annonceRepository.findOne(em, annonceId);

        assertNotNull(foundAnnonce, "L'annonce devrait être trouvée");
        assertEquals(annonceId, foundAnnonce.getId(), "L'ID devrait correspondre");
        assertEquals("Annonce à trouver", foundAnnonce.getTitle(), "Le titre devrait correspondre");
        assertEquals("Description de l'annonce", foundAnnonce.getDescription(), "La description devrait correspondre");
        assertEquals("456 Avenue Test", foundAnnonce.getAdress(), "L'adresse devrait correspondre");
        assertEquals("find@test.com", foundAnnonce.getMail(), "L'email devrait correspondre");
    }

    @Test
    void testFindOne_WhenAnnonceDoesNotExist() {
        Long nonExistentId = 99999L;
        Annonce foundAnnonce = annonceRepository.findOne(em, nonExistentId);

        assertNull(foundAnnonce, "L'annonce ne devrait pas être trouvée pour un ID inexistant");
    }

    @Test
    void testUpdate() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Category category = Category.builder()
                .label("Test Category")
                .build();

        Annonce annonce = Annonce.builder()
                .title("Titre original")
                .description("Description originale")
                .adress("123 Rue Original")
                .mail("original@test.com")
                .date(new Timestamp(System.currentTimeMillis()))
                .author(user)
                .category(category)
                .build();

        em.getTransaction().begin();
        em.persist(user);
        em.persist(category);
        em.persist(annonce);
        em.getTransaction().commit();

        Long annonceId = annonce.getId();

        annonce.setTitle("Titre modifié");
        annonce.setDescription("Description modifiée");
        annonce.setMail("updated@test.com");

        em.getTransaction().begin();
        Annonce updatedAnnonce = annonceRepository.update(em, annonce);
        em.getTransaction().commit();

        assertNotNull(updatedAnnonce, "L'annonce mise à jour ne devrait pas être null");
        assertEquals(annonceId, updatedAnnonce.getId(), "L'ID ne devrait pas changer");
        assertEquals("Titre modifié", updatedAnnonce.getTitle(), "Le titre devrait être modifié");
        assertEquals("Description modifiée", updatedAnnonce.getDescription(), "La description devrait être modifiée");
        assertEquals("updated@test.com", updatedAnnonce.getMail(), "L'email devrait être modifié");

        em.clear();
        Annonce annonceFromDb = em.find(Annonce.class, annonceId);
        assertEquals("Titre modifié", annonceFromDb.getTitle(), "Le titre en base devrait être modifié");
        assertEquals("Description modifiée", annonceFromDb.getDescription(), "La description en base devrait être modifiée");
    }

    @Test
    void testDelete_WhenAnnonceExists() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        Category category = Category.builder()
                .label("Test Category")
                .build();

        Annonce annonce = Annonce.builder()
                .title("Annonce à supprimer")
                .description("Cette annonce sera supprimée")
                .adress("789 Boulevard Delete")
                .mail("delete@test.com")
                .date(new Timestamp(System.currentTimeMillis()))
                .author(user)
                .category(category)
                .build();

        em.getTransaction().begin();
        em.persist(user);
        em.persist(category);
        em.persist(annonce);
        em.getTransaction().commit();

        Long annonceId = annonce.getId();

        Annonce annonceBeforeDelete = em.find(Annonce.class, annonceId);
        assertNotNull(annonceBeforeDelete, "L'annonce devrait exister avant la suppression");

        em.getTransaction().begin();
        annonceRepository.delete(em, annonceId);
        em.getTransaction().commit();

        em.clear();
        Annonce annonceAfterDelete = em.find(Annonce.class, annonceId);
        assertNull(annonceAfterDelete, "L'annonce ne devrait plus exister après suppression");
    }

    @Test
    void testDelete_WhenAnnonceDoesNotExist() {
        Long nonExistentId = 99999L;

        em.getTransaction().begin();
        assertDoesNotThrow(() -> annonceRepository.delete(em, nonExistentId),
                "La suppression d'une annonce inexistante ne devrait pas lever d'exception");
        em.getTransaction().commit();
    }
}

