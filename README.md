# Compte Rendu du Projet e3-tp-25

## Présentation générale
Ce projet est une application Spring Boot qui gère un système de gestion scolaire avec des étudiants, des professeurs et des cours. Il s'agit d'une API RESTful qui permet de manipuler ces différentes entités via des endpoints HTTP.

## Technologies utilisées
- **Java 17** : Langage de programmation principal
- **Spring Boot 3.5.0** : Framework d'application
- **Spring Data JPA** : Pour l'accès aux données et la persistance
- **MySQL** : Système de gestion de base de données pour l'environnement de production
- **H2 Database** : Base de données en mémoire pour les tests d'intégration
- **JUnit 5** : Framework de test unitaire et d'intégration
- **Mockito** : Framework de mock pour les tests
- **Lombok** : Bibliothèque pour réduire le code boilerplate
- **Maven** : Outil de gestion de projet et de dépendances

## Structure du projet
L'application suit une architecture en couches typique d'une application Spring Boot :

```
e3-tp-25/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/
│   │   │       └── fbansept/
│   │   │           └── e3tp25/
│   │   │               ├── controller/  # Contrôleurs REST
│   │   │               ├── dao/         # Interfaces d'accès aux données
│   │   │               ├── dto/         # Objets de transfert de données
│   │   │               ├── exception/   # Exceptions personnalisées
│   │   │               ├── model/       # Modèles de données / entités JPA
│   │   │               ├── service/     # Couche service avec la logique métier
│   │   │               └── E3Tp25Application.java  # Point d'entrée
│   │   └── resources/
│   │       ├── application.properties      # Configuration principale
│   │       └── data.sql                  # Données initiales
│   └── test/
│       ├── java/
│       │   └── edu/fbansept/e3tp25/
│       │       ├── config/                  # Configuration des tests
│       │       ├── integration/             # Tests d'intégration
│       │       ├── service/                # Tests des services
│       │       └── unitaire/                # Tests unitaires
│       └── resources/
│           └── application-test.properties # Configuration spécifique aux tests
├── pom.xml    # Fichier de configuration Maven
└── .env       # Variables d'environnement
```

## Modèle de données
L'application utilise l'héritage de classes pour modéliser les différents types d'utilisateurs et de cours :

### Utilisateur
Classe de base abstraite pour tous les types d'utilisateurs :
- `id` : Identifiant unique (clé primaire)
- `email` : Adresse email unique (obligatoire)
- `password` : Mot de passe (obligatoire)
- `nomRole` : Type d'utilisateur (ETUDIANT, PROFESSEUR ou ADMINISTRATEUR)

L'héritage est implémenté via une stratégie de jointure (`InheritanceType.JOINED`) avec un discriminateur pour différencier les types d'utilisateurs.

### Etudiant
Sous-classe de `Utilisateur` représentant un étudiant :
- Hérite des attributs de `Utilisateur`
- `dateNaissance` : Date de naissance de l'étudiant

### Professeur
Sous-classe de `Utilisateur` représentant un professeur :
- Hérite des attributs de `Utilisateur`
- `anneesExperience` : Nombre d'années d'expérience

### Administrateur
Sous-classe de `Utilisateur` représentant un administrateur :
- Hérite des attributs de `Utilisateur`
- Aucun attribut spécifique supplémentaire

### Cours
Classe abstraite de base représentant un cours :
- `id` : Identifiant unique (clé primaire)
- `nom` : Nom du cours (obligatoire)
- `debut` : Date et heure de début du cours (obligatoire)
- `duree` : Durée du cours en minutes (obligatoire)
- `etudiantList` : Liste des étudiants inscrits au cours (relation many-to-many)
- `professeur` : Professeur qui enseigne le cours (relation many-to-one)

L'héritage est implémenté via une stratégie de jointure (`InheritanceType.JOINED`) avec un discriminateur (`type_cours`) pour différencier les types de cours.

### Presentiel
Sous-classe de `Cours` représentant un cours en présentiel :
- Hérite des attributs de `Cours`
- `salle` : Salle dans laquelle le cours a lieu (relation many-to-one)

### Distanciel
Sous-classe de `Cours` représentant un cours à distance :
- Hérite des attributs de `Cours`
- `lienReunion` : Lien de connexion pour la réunion virtuelle

### Salle
Entité représentant une salle de cours :
- `id` : Identifiant unique (clé primaire)
- `nom` : Nom de la salle (obligatoire)
- `capacite` : Nombre maximum d'étudiants que la salle peut accueillir (obligatoire)
- `coursPresentiels` : Liste des cours présentiels assignés à cette salle (relation one-to-many)

## API REST
L'application expose plusieurs endpoints REST pour manipuler les entités :

### API Cours (`/api/cours`)
- `GET /api/cours/liste` : Récupérer tous les cours
- `GET /api/cours/presentiel/liste` : Récupérer tous les cours présentiels
- `GET /api/cours/distanciel/liste` : Récupérer tous les cours à distance
- `GET /api/cours/{id}` : Récupérer un cours par son ID
- `POST /api/cours/creer` : Créer un nouveau cours (présentiel ou distanciel)

## Système de Tests

L'application est dotée d'un système complet de tests unitaires et d'intégration pour assurer la fiabilité du code. Nous utilisons JUnit 5 comme framework de test et Mockito pour simuler les dépendances.

### Structure des Tests

Les tests sont organisés en plusieurs catégories :

1. **Tests Unitaires** : Tests isolés des composants individuels, utilisant des mocks pour les dépendances.
   - `unitaire/EtudiantControllerTest.java` - Tests du contrôleur des étudiants
   - Plus d'autres tests unitaires pour vérifier le comportement isolé des classes

2. **Tests de Services** : Tests qui vérifient la logique métier des services.
   - `service/CoursServiceTest.java` - Tests des méthodes du service des cours, y compris la gestion des conflits horaires

3. **Tests d'Intégration** : Tests qui vérifient l'interaction entre plusieurs composants.
   - `integration/CoursServiceIntegrationTest.java` - Vérifie l'interaction entre le service Cours et la base de données
   - `integration/CoursControllerIntegrationTest.java` - Vérifie les endpoints REST avec un contexte Spring complet

### Configuration de la Base de Données de Test

Pour les tests, nous utilisons une base de données H2 en mémoire, configurée via le profil "test" :

1. **application-test.properties**
   ```properties
   # Configuration de la base de données H2 en mémoire pour les tests
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.username=sa
   spring.datasource.password=
   spring.datasource.driverClassName=org.h2.Driver
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
   spring.jpa.hibernate.ddl-auto=create-drop
   
   # Désactive le chargement automatique des scripts SQL
   spring.sql.init.mode=never
   
   # Activer les logs SQL détaillés pour le débogage
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   
   # Configuration pour éviter les conflits de séquence d'ID
   spring.jpa.properties.hibernate.id.new_generator_mappings=true
   ```

2. **Initialisation Programmatique des Données de Test**
   Au lieu d'utiliser des scripts SQL qui peuvent causer des problèmes avec les séquences d'ID, nous utilisons une approche programmatique pour initialiser les données de test via la classe `TestDataInitializer`. Cette classe est annotée avec `@Component` et `@Profile("test")` pour s'assurer qu'elle n'est utilisée que dans l'environnement de test.

### Gestion des Conflits Horaires dans les Tests

Un aspect important de l'application est la gestion des conflits horaires entre cours. Notre approche de test pour cette fonctionnalité comprend :

1. **Tests Unitaires** : Vérifiant que les méthodes de détection de conflit fonctionnent correctement avec des données simulées.

2. **Tests d'Intégration** : Vérifiant que le système complet gère correctement les conflits lors de la création de cours avec des horaires qui se chevauchent.

### Stratégies de Test

1. **Filtrage de Conflits en Java** : Plutôt que d'utiliser des requêtes JPQL complexes (qui peuvent poser problème avec H2), nous avons implémenté la logique de détection de conflits horaires en Java dans la couche service.

2. **Vérifications Robustes contre les NullPointerException** : Nous avons ajouté des vérifications nulls dans les méthodes critiques pour éviter les NullPointerException lors du traitement des cours avec des données manquantes.

3. **Utilisation de `lenient()` avec Mockito** : Pour éviter les erreurs `UnnecessaryStubbingException`, nous utilisons `lenient()` sur les stubs qui pourraient ne pas être utilisés dans certains scénarios de test.

### Exécution des Tests

Pour exécuter tous les tests avec le profil "test" activé, utilisez la commande Maven suivante :

```bash
mvn test -Dspring.profiles.active=test
```

Pour exécuter un test spécifique, utilisez la commande :

```bash
mvn test -Dtest=NomDeLaClasseDeTest -Dspring.profiles.active=test
```

Exemples :
- Pour exécuter uniquement les tests d'intégration du contrôleur de cours :
  ```bash
  mvn test -Dtest=CoursControllerIntegrationTest -Dspring.profiles.active=test
  ```

- Pour exécuter uniquement les tests unitaires du service de cours :
  ```bash
  mvn test -Dtest=CoursServiceTest -Dspring.profiles.active=test
  ```

### Dépannage des Tests

En cas d'échec des tests, voici quelques pistes de résolution :

1. **Problèmes de séquences d'ID** : Si des erreurs surviennent concernant les ID des entités, vérifiez que l'initialisation des données de test respecte les séquences d'ID générées automatiquement.

2. **Conflits horaires inattendus** : Si les tests de conflit horaire échouent, vérifiez que les dates et durées des cours de test sont correctement définies pour provoquer ou éviter des chevauchements.

3. **Stubs Mockito non utilisés** : Si Mockito génère des erreurs `UnnecessaryStubbingException`, utilisez `lenient()` pour les stubs qui pourraient ne pas être appelés dans tous les scénarios de test.

4. **Problèmes de persistance** : Si des entités ne sont pas correctement sauvegardées ou récupérées, vérifiez les relations JPA et assurez-vous que toutes les entités associées sont correctement initialisées.

## API REST (suite)

- `PUT /api/cours/{id}` : Mettre à jour un cours existant
- `DELETE /api/cours/{id}` : Supprimer un cours

### API Etudiant (`/api/etudiant`)
- `GET /api/etudiant/liste` : Récupérer tous les étudiants
- `GET /api/etudiant/{id}` : Récupérer un étudiant par son ID
- `POST /api/etudiant` : Créer un nouvel étudiant
- `PUT /api/etudiant/{id}` : Mettre à jour un étudiant existant (email et password sont protégés)
- `DELETE /api/etudiant/{id}` : Supprimer un étudiant

### API Professeur (`/api/professeur`)
- `GET /api/professeur/liste` : Récupérer tous les professeurs
- `GET /api/professeur/{id}` : Récupérer un professeur par son ID
- `POST /api/professeur` : Créer un nouveau professeur
- `PUT /api/professeur/{id}` : Mettre à jour un professeur existant (email et password sont protégés)
- `DELETE /api/professeur/{id}` : Supprimer un professeur

### API Salle (`/api/salle`)
- `GET /api/salle/liste` : Récupérer toutes les salles
- `GET /api/salle/{id}` : Récupérer une salle par son ID
- `GET /api/salle/disponibles` : Récupérer les salles disponibles pour une période donnée
- `POST /api/salle` : Créer une nouvelle salle

## Configuration
La configuration de l'application se fait via le fichier `application.properties` et les variables d'environnement :

- Le serveur utilise le port spécifié par la variable `SERVER_PORT`
- La base de données est configurée via les variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, et `DB_PASSWORD`
- Les options de génération du schéma de base de données sont définies par `DDL_AUTO`
- L'initialisation des données est contrôlée par `SQL_INIT_PLATFORM` et `SQL_INIT_MODE`

## Jeu de données de test
Le fichier `data-donnees-test.sql` contient des données initiales pour tester l'application :
- 2 étudiants avec les IDs 1 et 2
- 2 professeurs avec les IDs 3 et 4
- 1 administrateur avec l'ID 5
- 4 salles avec différentes capacités
- 3 cours (2 présentiels et 1 distanciel)

## Particularités techniques notables
1. **Héritage d'entités JPA** : Utilisation de la stratégie de jointure et d'un discriminateur pour les différents types d'utilisateurs et de cours
2. **Protection des données sensibles** : Les méthodes de mise à jour des utilisateurs protègent l'email et le mot de passe contre les modifications non autorisées
3. **Configuration via variables d'environnement** : Utilisation de fichiers `.env` et `.local.env` pour la configuration
4. **Configuration du fuseau horaire** : L'application est configurée pour utiliser UTC comme fuseau horaire par défaut
5. **Gestion des conflits d'horaires** : Vérification des conflits d'horaires pour les professeurs, étudiants et salles lors de la création d'un cours
6. **Vérification de capacité des salles** : Contrôle que la capacité des salles est suffisante pour accueillir tous les étudiants inscrits
7. **Service métier dédié** : Utilisation d'une couche service pour centraliser la logique métier et les validations
8. **Gestion d'exceptions personnalisées** : Utilisation d'exceptions dédiées pour les erreurs métier (conflits, capacité)

## Démarrage de l'application
L'application peut être démarrée en exécutant la méthode `main` de la classe `E3Tp25Application`. Elle nécessite une base de données MySQL accessible avec les paramètres définis dans les variables d'environnement.

## Migration de la base de données
Pour migrer une base de données existante vers la nouvelle structure, le script `migration.sql` est fourni. Il ajoute les nouvelles tables et colonnes nécessaires pour supporter les fonctionnalités avancées de gestion des cours.

## Exemples d'utilisation

### Créer un cours présentiel
```bash
curl -X POST http://localhost:8080/api/cours/creer \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Algorithmique",
    "debut": "2025-06-20T10:00:00",
    "duree": 120,
    "professeurId": 3,
    "etudiantIds": [1, 2],
    "typeCours": "PRESENTIEL",
    "salleId": 1
  }'
```

### Créer un cours distanciel
```bash
curl -X POST http://localhost:8080/api/cours/creer \
  -H "Content-Type: application/json" \
  -d '{
    "nom": "Base de données",
    "debut": "2025-06-20T14:00:00",
    "duree": 90,
    "professeurId": 4,
    "etudiantIds": [1],
    "typeCours": "DISTANCIEL",
    "lienReunion": "https://teams.microsoft.com/l/meetup-join/..."
  }'
```
