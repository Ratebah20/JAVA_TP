-- Données de test pour la base H2 en mémoire
-- Utilisateurs : étudiants, professeurs et admin
INSERT INTO utilisateur (id, email, password, nom_role)
VALUES  (1, 'etudiant1@test.com', 'test123', 'ETUDIANT'),
        (2, 'etudiant2@test.com', 'test123', 'ETUDIANT'),
        (3, 'professeur1@test.com', 'test123', 'PROFESSEUR'),
        (4, 'professeur2@test.com', 'test123', 'PROFESSEUR'),
        (5, 'admin@test.com', 'admin123', 'ADMINISTRATEUR');

-- Étudiants
INSERT INTO etudiant (date_naissance, id)
VALUES  ('2000-01-15', 1),
        ('2001-03-22', 2);

-- Professeurs
INSERT INTO professeur (annees_experience, id)
VALUES  (5, 3),
        (10, 4);

-- Administrateur
INSERT INTO administrateur (id)
VALUES  (5);

-- Salles
INSERT INTO salle (id, nom, capacite)
VALUES  (1, 'Salle Test A', 30),
        (2, 'Salle Test B', 15),
        (3, 'Salle Test C', 50);

-- Cours
INSERT INTO cours (id, nom, debut, duree, professeur_id, type_cours)
VALUES  
    (100, 'Cours Test 1', '2025-07-01 09:00:00', 90, 3, 'PRESENTIEL'),
    (101, 'Cours Test 2', '2025-07-01 14:00:00', 120, 4, 'PRESENTIEL'),
    (102, 'Cours Test 3', '2025-07-02 10:00:00', 60, 3, 'DISTANCIEL');

-- Inscriptions des étudiants aux cours
INSERT INTO cours_etudiant (cours_id, etudiant_id)
VALUES  (100, 1),
        (100, 2),
        (101, 1),
        (102, 2);

-- Données pour les cours présentiels
INSERT INTO presentiel (id, salle_id)
VALUES  (100, 1),
        (101, 3);

-- Données pour les cours distanciels
INSERT INTO distanciel (id, lien_reunion)
VALUES  (102, 'https://reunion-test.com/abc123');

-- Réinitialiser les séquences pour qu'elles commencent après les IDs utilisés
ALTER TABLE utilisateur ALTER COLUMN id RESTART WITH 100;
ALTER TABLE cours ALTER COLUMN id RESTART WITH 200;
ALTER TABLE salle ALTER COLUMN id RESTART WITH 100;
