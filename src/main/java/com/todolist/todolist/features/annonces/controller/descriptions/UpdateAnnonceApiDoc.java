package com.todolist.todolist.features.annonces.controller.descriptions;

import com.todolist.todolist.features.annonces.dto.AnnonceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
        summary = "Modifier une annonce",
        description = """
                Met à jour une annonce existante.
                - Seul l'auteur ou un administrateur peut modifier l'annonce.
                - Une annonce au statut PUBLISHED ou ARCHIVED ne peut être modifiée que par un administrateur.
                - Passer une annonce au statut ARCHIVED est réservé aux administrateurs.
                """
)
@ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Annonce mise à jour avec succès",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AnnonceDTO.class)
                )
        ),
        @ApiResponse(responseCode = "400", description = "Corps de la requête invalide (champs manquants ou mal formés)", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non authentifié", content = @Content),
        @ApiResponse(responseCode = "403", description = "Action non autorisée (non auteur ou tentative d'archivage sans droits admin)", content = @Content),
        @ApiResponse(responseCode = "404", description = "Annonce, auteur ou catégorie introuvable", content = @Content),
        @ApiResponse(responseCode = "422", description = "Annonce non modifiable (statut PUBLISHED ou ARCHIVED)", content = @Content),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
})
public @interface UpdateAnnonceApiDoc {
}
