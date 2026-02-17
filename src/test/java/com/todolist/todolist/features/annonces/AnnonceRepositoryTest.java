package com.todolist.todolist.features.annonces;

import com.todolist.todolist.features.annonces.entity.Annonce;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.annonces.repository.AnnonceRepository;
import com.todolist.todolist.features.categories.entity.Category;
import com.todolist.todolist.features.users.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceRepositoryTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static AnnonceRepository repository;

    private static Long annonceActiveId;
    private static Long annonceDraftId;

    @BeforeAll
    static void setUp() {
        emf = Persistence.createEntityManagerFactory("testUnit");
        em = emf.createEntityManager();
        repository = new AnnonceRepository();

        em.getTransaction().begin();

        User author = new User();
        author.setUsername("testuser");
        author.setEmail("testuser@mail.com");
        author.setPassword("1234");
        em.persist(author);

        Category category1 = new Category();
        category1.setLabel("Immobilier");
        em.persist(category1);

        Category category2 = new Category();
        category2.setLabel("Véhicules");
        em.persist(category2);

        for (int i = 1; i <= 7; i++) {
            Annonce a = Annonce.builder()
                    .title("Appartement " + i)
                    .description("Beau logement numéro " + i)
                    .adress("Paris")
                    .mail("contact" + i + "@test.com")
                    .date(Timestamp.from(Instant.now()))
                    .status(StatusEnum.PUBLISHED)
                    .author(author)
                    .category(category1)
                    .build();
            em.persist(a);
            if (i == 1) annonceActiveId = a.getId();
        }

        Annonce draft = Annonce.builder()
                .title("Voiture d'occasion")
                .description("Très bon état")
                .adress("Lyon")
                .mail("voiture@test.com")
                .date(Timestamp.from(Instant.now()))
                .status(StatusEnum.DRAFT)
                .author(author)
                .category(category2)
                .build();
        em.persist(draft);

        em.getTransaction().commit();

        annonceActiveId = em.createQuery("SELECT a.id FROM Annonce a WHERE a.title = 'Appartement 1'", Long.class)
                .getSingleResult();
        annonceDraftId = em.createQuery("SELECT a.id FROM Annonce a WHERE a.title = 'Voiture d''occasion'", Long.class)
                .getSingleResult();
    }

    @AfterAll
    static void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    @Test
    @Order(1)
    void searchWithFilters_noFilter_returnsAllResults() {
        List<Annonce> results = repository.searchWithFilters(em, null, null, null, 0, 10);
        assertEquals(8, results.size()); // 7 ACTIVE + 1 DRAFT
    }

    @Test
    @Order(2)
    void searchWithFilters_withKeyword_returnsMatching() {
        List<Annonce> results = repository.searchWithFilters(em, "appartement", null, null, 0, 10);
        assertEquals(7, results.size());
        assertTrue(results.stream().allMatch(a -> a.getTitle().toLowerCase().contains("appartement")));
    }

    @Test
    @Order(3)
    void searchWithFilters_withStatus_returnsOnlyMatchingStatus() {
        List<Annonce> results = repository.searchWithFilters(em, null, null, StatusEnum.DRAFT, 0, 10);
        assertEquals(1, results.size());
        assertEquals(StatusEnum.DRAFT, results.get(0).getStatus());
    }

    @Test
    @Order(4)
    void searchWithFilters_pagination_firstPage() {
        List<Annonce> page0 = repository.searchWithFilters(em, "appartement", null, StatusEnum.PUBLISHED, 0, 5);
        assertEquals(5, page0.size());
    }

    @Test
    @Order(5)
    void searchWithFilters_pagination_secondPage() {
        List<Annonce> page1 = repository.searchWithFilters(em, "appartement", null, StatusEnum.PUBLISHED, 1, 5);
        assertEquals(2, page1.size()); // 7 ACTIVE - 5 sur page 0 = 2 restants
    }

    @Test
    @Order(6)
    void searchWithFilters_pagination_noOverlapBetweenPages() {
        List<Annonce> page0 = repository.searchWithFilters(em, "appartement", null, StatusEnum.PUBLISHED, 0, 5);
        List<Annonce> page1 = repository.searchWithFilters(em, "appartement", null, StatusEnum.PUBLISHED, 1, 5);

        List<Long> ids0 = page0.stream().map(Annonce::getId).toList();
        List<Long> ids1 = page1.stream().map(Annonce::getId).toList();

        assertTrue(ids0.stream().noneMatch(ids1::contains));
    }

    @Test
    @Order(7)
    void searchWithFilters_withCategoryId_returnsOnlyThatCategory() {
        Long categoryId = em.createQuery("SELECT c.id FROM Category c WHERE c.label = 'Véhicules'", Long.class)
                .getSingleResult();
        List<Annonce> results = repository.searchWithFilters(em, null, categoryId, null, 0, 10);
        assertEquals(1, results.size());
        assertEquals("Voiture d'occasion", results.get(0).getTitle());
    }

    @Test
    @Order(8)
    void searchWithFilters_emptyPage_returnsEmptyList() {
        List<Annonce> results = repository.searchWithFilters(em, "appartement", null, StatusEnum.PUBLISHED, 99, 5);
        assertTrue(results.isEmpty());
    }

    @Test
    @Order(9)
    void countWithFilters_noFilter_returnsTotal() {
        long count = repository.countWithFilters(em, null, null, null);
        assertEquals(8, count);
    }

    @Test
    @Order(10)
    void countWithFilters_withKeyword_returnsCorrectCount() {
        long count = repository.countWithFilters(em, "Voiture", null, null);
        assertEquals(1, count);
    }

    @Test
    @Order(11)
    void countWithFilters_withStatus_returnsCorrectCount() {
        long count = repository.countWithFilters(em, null, null, StatusEnum.PUBLISHED);
        assertEquals(7, count);
    }

    @Test
    @Order(12)
    void count_returnsTotal() {
        long count = repository.count(em);
        assertEquals(8, count);
    }

    @Test
    @Order(13)
    void findOne_existingId_returnsAnnonce() {
        Annonce found = repository.findOne(em, annonceActiveId);
        assertNotNull(found);
        assertEquals("Appartement 1", found.getTitle());
    }

    @Test
    @Order(14)
    void findOne_nonExistingId_returnsNull() {
        Annonce found = repository.findOne(em, 9999L);
        assertNull(found);
    }

    @Test
    @Order(15)
    void create_persistsAnnonce() {
        User author = em.createQuery("SELECT u FROM User u", User.class).setMaxResults(1).getSingleResult();
        Category category = em.createQuery("SELECT c FROM Category c", Category.class).setMaxResults(1).getSingleResult();

        Annonce newAnnonce = Annonce.builder()
                .title("Nouvelle annonce")
                .description("Description test")
                .adress("Marseille")
                .mail("new@test.com")
                .date(Timestamp.from(Instant.now()))
                .status(StatusEnum.PUBLISHED)
                .author(author)
                .category(category)
                .build();

        em.getTransaction().begin();
        Annonce created = repository.create(em, newAnnonce);
        em.getTransaction().commit();

        assertNotNull(created.getId());
        assertEquals("Nouvelle annonce", repository.findOne(em, created.getId()).getTitle());
    }

    @Test
    @Order(16)
    void update_changesFields() {
        Annonce annonce = repository.findOne(em, annonceActiveId);
        annonce.setTitle("Titre modifié");

        em.getTransaction().begin();
        repository.update(em, annonce);
        em.getTransaction().commit();

        em.clear(); // vide le cache de premier niveau
        Annonce updated = repository.findOne(em, annonceActiveId);
        assertEquals("Titre modifié", updated.getTitle());
    }

    @Test
    @Order(17)
    void delete_removesAnnonce() {
        em.getTransaction().begin();
        repository.delete(em, annonceDraftId);
        em.getTransaction().commit();

        assertNull(repository.findOne(em, annonceDraftId));
    }

    @Test
    @Order(18)
    void delete_nonExistingId_doesNotThrow() {
        em.getTransaction().begin();
        assertDoesNotThrow(() -> repository.delete(em, 9999L));
        em.getTransaction().commit();
    }
}
