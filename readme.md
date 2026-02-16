## Architecture

```text
src
├── main
│   ├── java
│   │   └── com.todolist.todolist
│   │       ├── features
│   │       │   ├── annonces
│   │       │       ├── controller
│   │       │       ├── dto
│   │       │       ├── entity
│   │       │       ├── enums
│   │       │       ├── exceptions
│   │       │       ├── mappers
│   │       │       ├── repository
│   │       │       ├── service
│   │       │       └── utils
│   │       │   ├── auth
│   │       │   ├── categories
│   │       │   └── users
│   │       │
│   │       ├── config -> Fichiers de configuration
│   │       ├── database -> Fichiers liés à la BD et à persistence.xml
│   │       └── utils -> Fichiers utilitaires
│   │
│   └── resources
│       └── META-INF -> persistence.xml
│
│
└── test
    ├── java
    │   └── com.todolist.todolist.annonces
    └── resources
        └── META-INF -> persistence.xml (pour les tests)
```

## Choix Jersey/RESTEasy 

Pour ce choix, j'ai demandé à Claude en lui fournissant mon pom.xml. 
Il m'a conseillé de prendre Jersey plutôt que RESTEasy car Jersey s'adapte bien à Tomcat et à l'environnement Jakarta.
Je suis donc parti sur ce choix.


## INFOS
- Documentation Swagger disponible à cette URL : http://localhost:8080/ToDoList/swagger-ui.html 
- Comme le sujet ne parlait pas de JWT, mais d'un "token simple", je n'ai pas utilisé de librairie particulière pour avoir un JWT.