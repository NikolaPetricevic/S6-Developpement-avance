package com.todolist.todolist.annonces;

import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.annonces.repository.AnnonceRepository;
import com.todolist.todolist.annonces.utils.AnnonceSearchCriteria;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceRepositoryFiltersTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnnonceRepository annonceRepository;

    // Données de test réutilisables
    private User user1;
    private User user2;
    private Category category1;
    private Category category2;

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

        // Créer des données de test communes
        createTestData();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            try {
                em.getTransaction().begin();
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

    /**
     * Crée un jeu de données de test avec :
     * - 2 utilisateurs
     * - 2 catégories
     * - 10 annonces variées
     */
    private void createTestData() {
        em.getTransaction().begin();

        // Créer les utilisateurs
        user1 = User.builder()
                .username("alice")
                .email("alice@test.com")
                .password("password123")
                .build();

        user2 = User.builder()
                .username("bob")
                .email("bob@test.com")
                .password("password123")
                .build();

        em.persist(user1);
        em.persist(user2);

        // Créer les catégories
        category1 = Category.builder()
                .label("Informatique")
                .build();

        category2 = Category.builder()
                .label("Mobilier")
                .build();

        em.persist(category1);
        em.persist(category2);

        // Créer 10 annonces avec différentes combinaisons
        createAnnonce("Vends ordinateur portable", "Ordinateur Dell en excellent état", user1, category1);
        createAnnonce("Recherche développeur Java", "Projet Java Spring Boot", user1, category1);
        createAnnonce("Formation Python débutant", "Apprendre Python facilement", user2, category1);
        createAnnonce("PC gamer à vendre", "Configuration haut de gamme", user2, category1);
        createAnnonce("Clavier mécanique", "Clavier gaming RGB", user1, category1);

        createAnnonce("Table basse en bois", "Meuble design scandinave", user1, category2);
        createAnnonce("Chaise de bureau", "Chaise ergonomique confortable", user2, category2);
        createAnnonce("Étagère murale", "Rangement mural moderne", user1, category2);
        createAnnonce("Canapé 3 places", "Canapé en cuir noir", user2, category2);
        createAnnonce("Bureau informatique", "Grand bureau pour ordinateur", user1, category2);

        em.getTransaction().commit();
    }

    private void createAnnonce(String title, String description, User author, Category category) {
        Annonce annonce = Annonce.builder()
                .title(title)
                .description(description)
                .adress("123 Rue Test")
                .mail("test@example.com")
                .date(new Timestamp(System.currentTimeMillis()))
                .author(author)
                .category(category)
                .build();
        em.persist(annonce);
    }

    // ==================== TESTS DE PAGINATION ====================

    @Test
    void testPagination_FirstPage() {
        // Arrange
        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // Act - Récupérer la première page (3 éléments)
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 3);

        // Assert
        assertEquals(3, results.size(), "La première page devrait contenir 3 annonces");
    }

    @Test
    void testPagination_SecondPage() {
        // Arrange
        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // Act - Récupérer la deuxième page (3 éléments)
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 1, 3);

        // Assert
        assertEquals(3, results.size(), "La deuxième page devrait contenir 3 annonces");
    }

    @Test
    void testPagination_LastPage() {
        // Arrange
        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // Act - Récupérer la dernière page (page 3 avec taille 3, donc 1 élément restant)
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 3, 3);

        // Assert
        assertEquals(1, results.size(), "La dernière page devrait contenir 1 annonce");
    }

    @Test
    void testPagination_DifferentPageSizes() {
        // Arrange
        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // Act
        List<Annonce> page5 = annonceRepository.findByCriteria(em, criteria, 0, 5);
        List<Annonce> page2 = annonceRepository.findByCriteria(em, criteria, 0, 2);

        // Assert
        assertEquals(5, page5.size(), "Page de taille 5 devrait contenir 5 annonces");
        assertEquals(2, page2.size(), "Page de taille 2 devrait contenir 2 annonces");
    }

    @Test
    void testPagination_EmptyPage() {
        // Arrange
        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // Act - Page au-delà des données disponibles
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 10, 3);

        // Assert
        assertTrue(results.isEmpty(), "Une page au-delà des données devrait être vide");
    }

    // ==================== TESTS DE FILTRAGE PAR MOT-CLÉ ====================

    @Test
    void testFilter_ByKeyword_InTitle() {
        // Arrange - Rechercher "ordinateur" dans le titre
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("ordinateur")
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertEquals(2, results.size(), "Devrait trouver 2 annonces contenant 'ordinateur' dans le titre");
        assertTrue(results.stream().allMatch(a ->
                a.getTitle().toLowerCase().contains("ordinateur") ||
                        a.getDescription().toLowerCase().contains("ordinateur")
        ), "Toutes les annonces devraient contenir 'ordinateur'");
    }

    @Test
    void testFilter_ByKeyword_InDescription() {
        // Arrange - Rechercher "gaming" dans la description
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("gaming")
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertTrue(results.size() >= 1, "Devrait trouver au moins 1 annonce avec 'gaming'");
        assertTrue(results.stream().anyMatch(a ->
                a.getDescription().toLowerCase().contains("gaming")
        ), "Au moins une annonce devrait contenir 'gaming' dans la description");
    }

    @Test
    void testFilter_ByKeyword_NoResults() {
        // Arrange - Mot-clé qui n'existe pas
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("moto")
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertTrue(results.isEmpty(), "Aucun résultat ne devrait être trouvé pour 'moto'");
    }

    @Test
    void testFilter_ByKeyword_PartialMatch() {
        // Arrange - Recherche partielle
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("ord")  // Devrait matcher "ordinateur"
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertTrue(results.size() >= 1, "Devrait trouver des annonces avec une correspondance partielle");
    }

    // ==================== TESTS DE FILTRAGE PAR CATÉGORIE ====================

    @Test
    void testFilter_ByCategory_Informatique() {
        // Arrange
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .categoryId(category1.getId())
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertEquals(5, results.size(), "Devrait trouver 5 annonces dans la catégorie Informatique");
        assertTrue(results.stream().allMatch(a ->
                a.getCategory().getId().equals(category1.getId())
        ), "Toutes les annonces devraient être dans la catégorie Informatique");
    }

    @Test
    void testFilter_ByCategory_Mobilier() {
        // Arrange
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .categoryId(category2.getId())
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertEquals(5, results.size(), "Devrait trouver 5 annonces dans la catégorie Mobilier");
        assertTrue(results.stream().allMatch(a ->
                a.getCategory().getId().equals(category2.getId())
        ), "Toutes les annonces devraient être dans la catégorie Mobilier");
    }

    @Test
    void testFilter_ByCategory_NonExistent() {
        // Arrange - Catégorie qui n'existe pas
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .categoryId(99999L)
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertTrue(results.isEmpty(), "Aucune annonce ne devrait être trouvée pour une catégorie inexistante");
    }

    // ==================== TESTS DE FILTRES COMBINÉS ====================

    @Test
    void testFilter_KeywordAndCategory() {
        // Arrange - Rechercher "bureau" dans la catégorie Mobilier
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("bureau")
                .categoryId(category2.getId())
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertTrue(results.size() >= 1, "Devrait trouver au moins 1 annonce");
        assertTrue(results.stream().allMatch(a ->
                a.getCategory().getId().equals(category2.getId())
        ), "Toutes les annonces devraient être dans la catégorie Mobilier");
        assertTrue(results.stream().anyMatch(a ->
                a.getTitle().toLowerCase().contains("bureau") ||
                        a.getDescription().toLowerCase().contains("bureau")
        ), "Au moins une annonce devrait contenir 'bureau'");
    }

    @Test
    void testFilter_KeywordAndCategory_NoResults() {
        // Arrange - Rechercher "Java" dans la catégorie Mobilier (incompatible)
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("Java")
                .categoryId(category2.getId())
                .build();

        // Act
        List<Annonce> results = annonceRepository.findByCriteria(em, criteria, 0, 10);

        // Assert
        assertTrue(results.isEmpty(), "Aucune annonce ne devrait correspondre à ces critères incompatibles");
    }

    // ==================== TESTS DE COUNT ====================

    @Test
    void testCount_NoCriteria() {
        // Arrange
        AnnonceSearchCriteria criteria = new AnnonceSearchCriteria();

        // Act
        long count = annonceRepository.countByCriteria(em, criteria);

        // Assert
        assertEquals(10, count, "Devrait compter 10 annonces au total");
    }

    @Test
    void testCount_WithKeyword() {
        // Arrange
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("ordinateur")
                .build();

        // Act
        long count = annonceRepository.countByCriteria(em, criteria);

        // Assert
        assertEquals(2, count, "Devrait compter 2 annonces avec 'ordinateur'");
    }

    @Test
    void testCount_WithCategory() {
        // Arrange
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .categoryId(category1.getId())
                .build();

        // Act
        long count = annonceRepository.countByCriteria(em, criteria);

        // Assert
        assertEquals(5, count, "Devrait compter 5 annonces dans la catégorie Informatique");
    }

    @Test
    void testCount_WithMultipleCriteria() {
        // Arrange
        AnnonceSearchCriteria criteria = AnnonceSearchCriteria.builder()
                .keyword("bureau")
                .categoryId(category2.getId())
                .build();

        // Act
        long count = annonceRepository.countByCriteria(em, criteria);

        // Assert
        assertTrue(count >= 1, "Devrait compter au moins 1 annonce correspondant aux critères");
    }
}
