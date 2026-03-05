## Architecture

```text
src
├── main
│   ├── java
│   │   └── com.todolist.todolist
│   │       ├── config/             -> Sécurité (Spring Security, JWT)
│   │       │   ├── jwt/
│   │       │   └── logging/
│   │       ├── exceptions/         -> Exceptions globales & handler
│   │       ├── utils/              -> Utilitaires partagés
│   │       └── features/           -> Fonctionnalités métier
│   │           ├── annonces/       -> controller, dto, entity, enums,
│   │           │                      exceptions, mapper, repository,
│   │           │                      service, utils
│   │           ├── auth/           -> controller, dto, exceptions, service
│   │           ├── categories/     -> controller, dto, entity, exceptions,
│   │           │                      mapper, repository, service
│   │           └── users/          -> dto, entity, enums, exceptions,
│   │                                  mappers, repository, service
│   └── resources
│       ├── application.yaml        -> Configuration (BDD, JWT, serveur...)
│       └── logback.xml             -> Configuration des logs
│
└── test
    ├── java
    │   └── com.todolist.todolist
    │       └── annonces/           -> AnnonceRepositoryTest,
    │                                  AnnonceServiceTest,
    │                                  AnnonceControllerTest
    └── resources
        └── application.yaml        -> Config de test (H2 en mémoire)
```


## INFOS
- Documentation Swagger disponible à cette URL : http://localhost:8080/api/swagger-ui/index.html
- L'exercice 13 n'est pas fait.

## Lancer l'application via Docker

**Pré-requis** : Docker et Docker Compose installés.

1. Copier le fichier d'exemple et renseigner les variables :
   ```bash
   cp .env.example .env
   # Éditer .env et remplir JWT_SECRET, DB_USERNAME, DB_PASSWORD
   ```

2. Construire et démarrer les containers :
   ```bash
   docker compose up --build
   ```

3. L'API est accessible sur `http://localhost:8080/api`

Pour arrêter et supprimer les containers (en conservant les données) :
```bash
docker compose down
```
Pour tout supprimer, volumes compris :
```bash
docker compose down -v
```

## Problèmes rencontrés
- Les mappers ne prenaient pas directement en compte les sous-entités comme 

"""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryEntity;

Il fallait donc préciser au mapper comment bien mapper les entités : 

"""

    @Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
    public interface AnnonceMapper {

      @Mapping(target = "author", ignore = true)
      @Mapping(target = "categoryEntity", ignore = true)
      AnnonceEntity toEntity(AnnonceDTO dto);

      @Mapping(target = "author_id", ignore = true)
      @Mapping(target = "category_id", ignore = true)
      @Mapping(source = "categoryEntity", target = "category")
      AnnonceDTO toDTO(AnnonceEntity entity);
    }

"""

- Utilisation de H2 au lieu de testcontainers : Je n'arrivais pas à faire fonctionner les testcontainers sur ma machine, et j'ai donc du passer sur H2 pour les tests du controller. Selon Claude, comme on n'utilise pas de fonctione spécifique à Postgre dans notre cadre, ça ne devrait pas poser problème.