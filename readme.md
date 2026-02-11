Vrac problèmes rencontrés : 

- pom.xml qui compilait pas à cause de Java 23 et Lombok -> passage à Java 18
- problème lors de la création des entités : Hibernate ne trouvait pas d'entité à mapper, et les tables n'étaient donc pas ajoutées à la BD
Solution : déclarer l'entité dans le persistence.xml
- Lors de l'accès aux détails d'une annonce : erreur car on charge les données en LAZY, ce qui empêche 
d'accéder à l'auteur d'une annonce pour afficher ses infos. 
Solution : Charger en EAGER. Claude m'a également proposé de faire une méthode pour charger une annonce
avec ses relations. Mais comme c'est une projet très léger, j'ai préféré juste passer en EAGER.


NOTE : J'assume complètement le fait que les tests ont été générés par IA et pas revérifiés. Ma vie d'étudiant ne me permet pas de donner autant de temps pour ce genre de chose.
