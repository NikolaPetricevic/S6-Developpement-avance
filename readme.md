## Architecture

```text
src
├── main
│   ├── java
│   │   └── com.todolist.todolist
│   │       ├── annonces -> feature
│   │       │   ├── entity
│   │       │   ├── enums
│   │       │   ├── repository
│   │       │   ├── service
│   │       │   ├── servlets
│   │       │   └── utils
│   │       ├── auth -> feature
│   │       ├── categories -> feature
│   │       ├── config -> Fichiers de configuration
│   │       ├── database -> Fichiers liés à la BD et à persistence.xml
│   │       ├── users -> feature
│   │       └── utils -> Fichiers utilitaires
│
│   ├── resources
│   │   └── META-INF -> persistence.xml
│
│   └── webapp -> package des pages JSP
│       └── WEB-INF -> web.xml
│
└── test
    ├── java
    │   └── com.todolist.todolist.annonces
    │       ├── repository
    │       ├── service
    │       └── servlets
    └── resources
        └── META-INF -> persistence.xml (pour les tests)
```

## Problèmes rencontrés et solutions :

- Au début du TP, j'utilisais Java 23, mais j'ai basculé sur Java 18 car j'utilise Lombok, et Java 23 empêchait Lombok de fonctionner correctement.
- Lors de la création des entités sur la base de données, Hibernate ne trouvait pas les entités à mapper, et les tables n'étaient pas créées sur la BD. J'ai donc déclaré les entités dans le fichier persistence.xml
- Lors de l'accès aux détails d'une annonce, une erreur se produisait et empêchait l'accès à la page. En fait, j'affiche le nom et le prénom de l'auteur sur la page, mais comme les données sont chargées en LAZY, on ne pouvait plus accéder à l'auteur pour afficher ses infos. J'ai donc changé le mode de LAZY à EAGER. Claude m'a également proposé de faire une méthode pour charger une annonce avec ses relations. Mais comme c'est une projet très léger, j'ai préféré juste passer en EAGER.


# 

NOTE : J'assume complètement le fait que les tests ont été générés par IA et pas revérifiés. Ma vie d'étudiant ne me permet pas de donner autant de temps pour cet exercice.
Les Tests d'intégration métier et les tests web ne sont d'ailleurs pas faits.