package com.todolist.todolist.annonces.servlets;

import com.todolist.todolist.annonces.entity.Annonce;
import com.todolist.todolist.annonces.enums.StatusEnum;
import com.todolist.todolist.annonces.service.AnnonceService;
import com.todolist.todolist.auth.service.AuthService;
import com.todolist.todolist.categories.entity.Category;
import com.todolist.todolist.categories.service.CategoryService;
import com.todolist.todolist.users.entity.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceAddTest {

    @Mock
    private AnnonceService annonceService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private RequestDispatcher requestDispatcher;

    private AnnonceAdd servlet;

    private User testUser;
    private Category testCategory;
    private List<Category> testCategories;

    @BeforeEach
    void setUp() throws Exception {
        // Initialisation des données de test
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        testCategory = Category.builder()
                .id(1L)
                .label("Test Category")
                .build();

        testCategories = Arrays.asList(testCategory);

        // Configuration du ServletConfig et ServletContext
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);

        // Création du servlet SANS appeler init() pour éviter la connexion à la base
        servlet = new AnnonceAdd();

        // Injection manuelle des services MOCKÉS via réflexion
        // Cela remplace les vrais services qui seraient créés dans init()
        injectField(servlet, "annonceService", annonceService);
        injectField(servlet, "categoryService", categoryService);
        injectField(servlet, "authService", authService);

        // PAS besoin d'appeler servlet.init() car on a déjà injecté les mocks
        // On initialise juste le ServletConfig pour éviter l'erreur
        injectField(servlet, "config", servletConfig);
    }

    /**
     * Méthode utilitaire pour injecter des champs privés via réflexion
     */
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Class<?> currentClass = target.getClass();
        Field field = null;

        // Recherche du champ dans la classe et ses parents (GenericServlet)
        while (currentClass != null && field == null) {
            try {
                field = currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }

        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found");
        }

        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testDoGet_ShouldLoadCategoriesAndForwardToJsp() throws ServletException, IOException {
        // Arrange
        when(categoryService.findAll()).thenReturn(testCategories);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(categoryService).findAll();
        verify(request).setAttribute("categories", testCategories);
        verify(servletContext).getRequestDispatcher("/annonce-add.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPost_WithValidData_ShouldCreateAnnonceAndRedirect() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("1");

        when(authService.getCurrentUser(session)).thenReturn(testUser);
        when(categoryService.findOne(1L)).thenReturn(testCategory);

        ArgumentCaptor<Annonce> annonceCaptor = ArgumentCaptor.forClass(Annonce.class);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(annonceService).createAnnonce(annonceCaptor.capture());
        verify(response).sendRedirect("annonce-list");

        Annonce capturedAnnonce = annonceCaptor.getValue();
        assertEquals("Test Title", capturedAnnonce.getTitle());
        assertEquals("Test Description", capturedAnnonce.getDescription());
        assertEquals("Test Address", capturedAnnonce.getAdress());
        assertEquals("test@example.com", capturedAnnonce.getMail());
        assertEquals(StatusEnum.DRAFT, capturedAnnonce.getStatus());
        assertEquals(testUser, capturedAnnonce.getAuthor());
        assertEquals(testCategory, capturedAnnonce.getCategory());
        assertNotNull(capturedAnnonce.getDate());
    }

    @Test
    void testDoPost_WithMissingTitle_ShouldShowError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(categoryService.findAll()).thenReturn(testCategories);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Tous les champs sont obligatoires !");
        verify(request).setAttribute("categories", testCategories);
        verify(requestDispatcher).forward(request, response);
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void testDoPost_WithNullDescription_ShouldShowError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("description")).thenReturn(null);
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(categoryService.findAll()).thenReturn(testCategories);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Tous les champs sont obligatoires !");
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void testDoPost_WithInvalidCategoryId_ShouldShowError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("invalid");
        when(categoryService.findAll()).thenReturn(testCategories);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("invalide"));
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void testDoPost_WithNoAuthenticatedUser_ShouldRedirectToLogin() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(authService.getCurrentUser(session)).thenReturn(null);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendRedirect("login");
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void testDoPost_WithNonExistentCategory_ShouldShowError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("999");

        when(authService.getCurrentUser(session)).thenReturn(testUser);
        when(categoryService.findOne(999L)).thenReturn(null);
        when(categoryService.findAll()).thenReturn(testCategories);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), contains("introuvable"));
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void testDoPost_WithWhitespaceOnlyFields_ShouldShowError() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("   ");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(request.getParameter("adress")).thenReturn("Test Address");
        when(request.getParameter("mail")).thenReturn("test@example.com");
        when(request.getParameter("categoryId")).thenReturn("1");
        when(categoryService.findAll()).thenReturn(testCategories);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(request).setAttribute("errorMessage", "Tous les champs sont obligatoires !");
        verify(annonceService, never()).createAnnonce(any());
    }

    @Test
    void testDoPost_ShouldTrimInputFields() throws ServletException, IOException {
        // Arrange
        when(request.getParameter("title")).thenReturn("  Test Title  ");
        when(request.getParameter("description")).thenReturn("  Test Description  ");
        when(request.getParameter("adress")).thenReturn("  Test Address  ");
        when(request.getParameter("mail")).thenReturn("  test@example.com  ");
        when(request.getParameter("categoryId")).thenReturn("1");

        when(authService.getCurrentUser(session)).thenReturn(testUser);
        when(categoryService.findOne(1L)).thenReturn(testCategory);

        ArgumentCaptor<Annonce> annonceCaptor = ArgumentCaptor.forClass(Annonce.class);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(annonceService).createAnnonce(annonceCaptor.capture());

        Annonce capturedAnnonce = annonceCaptor.getValue();
        assertEquals("Test Title", capturedAnnonce.getTitle());
        assertEquals("Test Description", capturedAnnonce.getDescription());
        assertEquals("Test Address", capturedAnnonce.getAdress());
        assertEquals("test@example.com", capturedAnnonce.getMail());
    }
}