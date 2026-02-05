package com.todolist.todolist.config;

import com.todolist.todolist.database.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.persistence.EntityManager;

@WebListener // <--- Cette annotation dit à Tomcat : "Lance cette classe au démarrage"
public class AppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("🚀 Démarrage de l'application - Initialisation de Hibernate...");

        // C'est ICI que la magie opère.
        // En demandant un EntityManager, on force la classe JPAUtil à charger
        // l'EntityManagerFactory. C'est à cet instant précis qu'Hibernate
        // lit persistence.xml et crée les tables (hbm2ddl.auto).
        EntityManager em = JPAUtil.getEntityManager();

        // On le ferme tout de suite, c'était juste pour "réveiller" le système.
        em.close();

        System.out.println("✅ Hibernate initialisé et tables vérifiées/créées !");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Arrêt de l'application - Fermeture de Hibernate...");
        // On ferme proprement la connexion à la base quand on coupe Tomcat
        JPAUtil.shutdown();
    }
}
