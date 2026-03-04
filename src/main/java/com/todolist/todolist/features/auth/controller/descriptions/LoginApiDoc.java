package com.todolist.todolist.features.auth.controller.descriptions;

import com.todolist.todolist.features.auth.dto.LoginResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "Se connecter et récupérer un token JWT",
        description = "Authentifie un utilisateur avec ses identifiants et retourne un token JWT."
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Connexion réussie",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = LoginResponseDTO.class),
                        examples = @ExampleObject(value = """
                                {
                                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                  "userId": 1,
                                  "username": "john",
                                  "role": "ROLE_USER"
                                }
                                """)
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Corps de la requête invalide (champs manquants ou mal formés)",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Mot de passe incorrect",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Utilisateur introuvable",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur",
                content = @Content
        )
})
public @interface LoginApiDoc {
}
