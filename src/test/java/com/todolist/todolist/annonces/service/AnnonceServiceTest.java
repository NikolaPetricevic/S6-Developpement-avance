package com.todolist.todolist.annonces.service;

import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.annonces.enums.StatusEnum;
import com.todolist.todolist.annonces.utils.AnnonceSearchCriteria;
import com.todolist.todolist.categories.entity.Category;
import com.todolist.todolist.users.entity.User;
import org.junit.jupiter.api.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceServiceTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private AnnonceService annonceService;

    @BeforeAll
    static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("testUnit");
    }

    @AfterAll
    static void tearDownClass() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        // Injection de l'EntityManagerFactory de test
        annonceService = new AnnonceService(entityManagerFactory);

        // Créer des données de test
        entityManager.getTransaction().begin();

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("1234")
                .build();
        entityManager.persist(user);

        Category category = Category.builder()
                .label("Test Category")
                .build();
        entityManager.persist(category);

        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            // Nettoyer les données après chaque test
            entityManager.getTransaction().begin();
            entityManager.createQuery("DELETE FROM Annonce").executeUpdate();
            entityManager.createQuery("DELETE FROM User").executeUpdate();
            entityManager.createQuery("DELETE FROM Category").executeUpdate();
            entityManager.getTransaction().commit();

            entityManager.close();
        }
    }

    @Test
    void testCreateAnnonce_Success() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce annonce = Annonce.builder()
                .title("Nouvelle annonce")
                .description("Description de l'annonce de test")
                .adress("123 Rue de Test")
                .mail("annonce@example.com")
                .date(new Timestamp(System.currentTimeMillis()))
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category)
                .build();

        // When
        Annonce createdAnnonce = annonceService.createAnnonce(annonce);

        // Then
        assertNotNull(createdAnnonce);
        assertNotNull(createdAnnonce.getId());
        assertEquals("Nouvelle annonce", createdAnnonce.getTitle());
        assertEquals("Description de l'annonce de test", createdAnnonce.getDescription());
        assertEquals(StatusEnum.DRAFT, createdAnnonce.getStatus());
    }

    @Test
    void testPublish_ShouldChangeStatusToPublished() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce annonce = Annonce.builder()
                .title("Annonce à publier")
                .description("Description")
                .adress("Adresse")
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(annonce);

        // When
        Annonce publishedAnnonce = annonceService.publish(createdAnnonce);

        // Then
        assertEquals(StatusEnum.PUBLISHED, publishedAnnonce.getStatus());
    }

    @Test
    void testUpdateAnnonce_Success() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce annonce = Annonce.builder()
                .title("Titre original")
                .description("Description originale")
                .adress("Adresse originale")
                .mail("original@example.com")
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(annonce);

        // When
        createdAnnonce.setTitle("Titre modifié");
        createdAnnonce.setDescription("Description modifiée");
        createdAnnonce.setMail("modified@example.com");
        Annonce updatedAnnonce = annonceService.updateAnnonce(createdAnnonce);

        // Then
        assertNotNull(updatedAnnonce);
        assertEquals("Titre modifié", updatedAnnonce.getTitle());
        assertEquals("Description modifiée", updatedAnnonce.getDescription());
        assertEquals("modified@example.com", updatedAnnonce.getMail());
    }

    @Test
    void testArchive_ShouldChangeStatusToArchived() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce annonce = Annonce.builder()
                .title("Annonce à archiver")
                .description("Description")
                .adress("Adresse")
                .status(StatusEnum.PUBLISHED)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(annonce);

        // When
        Annonce archivedAnnonce = annonceService.archive(createdAnnonce);

        // Then
        assertEquals(StatusEnum.ARCHIVED, archivedAnnonce.getStatus());
    }

    @Test
    void testFindOne_ExistingAnnonce() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce annonce = Annonce.builder()
                .title("Annonce à rechercher")
                .description("Description")
                .adress("Adresse")
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(annonce);

        // When
        Annonce foundAnnonce = annonceService.findOne(createdAnnonce.getId());

        // Then
        assertNotNull(foundAnnonce);
        assertEquals(createdAnnonce.getId(), foundAnnonce.getId());
        assertEquals("Annonce à rechercher", foundAnnonce.getTitle());
    }

    @Test
    void testFindOne_NonExistingAnnonce() {
        // When
        Annonce foundAnnonce = annonceService.findOne(999L);

        // Then
        assertNull(foundAnnonce);
    }

    @Test
    void testFindByCriteria_WithResults() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        // Créer plusieurs annonces
        for (int i = 1; i <= 5; i++) {
            Annonce annonce = Annonce.builder()
                    .title("Annonce " + i)
                    .description("Description " + i)
                    .adress("Adresse " + i)
                    .status(StatusEnum.PUBLISHED)
                    .author(author)
                    .category(category)
                    .build();
            annonceService.createAnnonce(annonce);
        }

        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();
        criteria.setStatus(StatusEnum.PUBLISHED);

        // When
        List<Annonce> annonces = annonceService.findByCriteria(criteria, 0, 10);

        // Then
        assertNotNull(annonces);
        assertEquals(5, annonces.size());
    }

    @Test
    void testFindByCriteria_WithPagination() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        // Créer 10 annonces
        for (int i = 1; i <= 10; i++) {
            Annonce annonce = Annonce.builder()
                    .title("Annonce " + i)
                    .description("Description " + i)
                    .adress("Adresse " + i)
                    .status(StatusEnum.PUBLISHED)
                    .author(author)
                    .category(category)
                    .build();
            annonceService.createAnnonce(annonce);
        }

        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // When - première page (3 résultats)
        List<Annonce> page1 = annonceService.findByCriteria(criteria, 0, 3);
        // When - deuxième page (3 résultats)
        List<Annonce> page2 = annonceService.findByCriteria(criteria, 1, 3);

        // Then
        assertEquals(3, page1.size());
        assertEquals(3, page2.size());
        assertNotEquals(page1.get(0).getId(), page2.get(0).getId());
    }

    @Test
    void testCountByCriteria() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        // Créer 3 annonces publiées et 2 brouillons
        for (int i = 1; i <= 3; i++) {
            Annonce annonce = Annonce.builder()
                    .title("Annonce publiée " + i)
                    .description("Description " + i)
                    .adress("Adresse " + i)
                    .status(StatusEnum.PUBLISHED)
                    .author(author)
                    .category(category)
                    .build();
            annonceService.createAnnonce(annonce);
        }

        for (int i = 1; i <= 2; i++) {
            Annonce annonce = Annonce.builder()
                    .title("Brouillon " + i)
                    .description("Description " + i)
                    .adress("Adresse " + i)
                    .status(StatusEnum.DRAFT)
                    .author(author)
                    .category(category)
                    .build();
            annonceService.createAnnonce(annonce);
        }

        AnnonceSearchCriteria criteriaPublished = new AnnonceSearchCriteria();
        criteriaPublished.setStatus(StatusEnum.PUBLISHED);

        AnnonceSearchCriteria criteriaDraft = new AnnonceSearchCriteria();
        criteriaDraft.setStatus(StatusEnum.DRAFT);

        // When
        long publishedCount = annonceService.countByCriteria(criteriaPublished);
        long draftCount = annonceService.countByCriteria(criteriaDraft);

        // Then
        assertEquals(3, publishedCount);
        assertEquals(2, draftCount);
    }

    @Test
    void testCreateAnnonce_WithNullTitle_ShouldFail() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce invalidAnnonce = Annonce.builder()
                .title(null) // nullable=false
                .description("Description")
                .adress("Adresse")
                .author(author)
                .category(category)
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            annonceService.createAnnonce(invalidAnnonce);
        });
    }

    @Test
    void testUpdateAnnonce_NonExistingAnnonce_ShouldFail() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce nonExistingAnnonce = Annonce.builder()
                .id(999L) // ID inexistant
                .title("Titre")
                .description("Description")
                .adress("Adresse")
                .author(author)
                .category(category)
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            annonceService.updateAnnonce(nonExistingAnnonce);
        });
    }

    @Test
    void testPublish_FromDraftToPublished() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce draftAnnonce = Annonce.builder()
                .title("Brouillon")
                .description("Description")
                .adress("Adresse")
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(draftAnnonce);
        assertEquals(StatusEnum.DRAFT, createdAnnonce.getStatus());

        // When
        Annonce publishedAnnonce = annonceService.publish(createdAnnonce);

        // Then
        assertEquals(StatusEnum.PUBLISHED, publishedAnnonce.getStatus());

        // Vérifier en base
        Annonce verifiedAnnonce = annonceService.findOne(publishedAnnonce.getId());
        assertEquals(StatusEnum.PUBLISHED, verifiedAnnonce.getStatus());
    }

    @Test
    void testArchive_FromPublishedToArchived() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce publishedAnnonce = Annonce.builder()
                .title("Annonce publiée")
                .description("Description")
                .adress("Adresse")
                .status(StatusEnum.PUBLISHED)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(publishedAnnonce);
        assertEquals(StatusEnum.PUBLISHED, createdAnnonce.getStatus());

        // When
        Annonce archivedAnnonce = annonceService.archive(createdAnnonce);

        // Then
        assertEquals(StatusEnum.ARCHIVED, archivedAnnonce.getStatus());

        // Vérifier en base
        Annonce verifiedAnnonce = annonceService.findOne(archivedAnnonce.getId());
        assertEquals(StatusEnum.ARCHIVED, verifiedAnnonce.getStatus());
    }

    @Test
    void testDeleteAnnonce_Success() {
        // Given
        User author = entityManager.createQuery("SELECT u FROM User u WHERE u.username = 'testuser'", User.class)
                .getSingleResult();
        Category category = entityManager.createQuery("SELECT c FROM Category c WHERE c.label = 'Test Category'", Category.class)
                .getSingleResult();

        Annonce annonce = Annonce.builder()
                .title("Annonce à supprimer")
                .description("Description")
                .adress("Adresse")
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category)
                .build();

        Annonce createdAnnonce = annonceService.createAnnonce(annonce);
        Long annonceId = createdAnnonce.getId();

        // When
        annonceService.deleteAnnonce(annonceId);

        // Then
        Annonce deletedAnnonce = annonceService.findOne(annonceId);
        assertNull(deletedAnnonce);
    }
}
