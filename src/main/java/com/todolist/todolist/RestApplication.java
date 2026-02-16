package com.todolist.todolist;

import com.todolist.todolist.exceptions.mappers.GenericExceptionMapper;
import com.todolist.todolist.features.annonces.controller.AnnonceController;
import com.todolist.todolist.exceptions.mappers.ValidationExceptionMapper;
import com.todolist.todolist.exceptions.mappers.ResourceNotFoundExceptionMapper;
import com.todolist.todolist.features.auth.controller.AuthController;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ServerProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(
                title = "ToDoList API",
                version = "1.0"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080/ToDoList",
                        description = "Serveur local"
                )
        }
)
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(HelloWorldController.class);
        classes.add(AnnonceController.class);
        classes.add(AuthController.class);

        classes.add(ValidationExceptionMapper.class);
        classes.add(ResourceNotFoundExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);

        classes.add(OpenApiResource.class);

        return classes;
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();

        // Permet d'activer la vérification automatiques des DTO et de renvoyer des 400 en cas d'erreur
        properties.put(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        properties.put(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);
        return properties;
    }
}