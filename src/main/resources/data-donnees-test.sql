insert into utilisateur (id, email, password, nom_role)
values  (1, 'a@a.com', 'root', 'ETUDIANT'),
    (2, 'b@b.com', 'root', 'ETUDIANT'),
    (3, 'c@c.com', 'root', 'PROFESSEUR'),
    (4, 'd@d.com', 'root', 'PROFESSEUR'),
    (5, 'admin@example.com', 'admin123', 'ADMINISTRATEUR');

insert into etudiant (date_naissance, id)
values  ('2000-05-27', 1),
    ('2001-05-20', 2);

insert into professeur (annees_experience, id)
values  (15, 3),
    (20, 4);

insert into administrateur (id)
values  (5);

-- Insertion des salles
insert into salle (id, nom, capacite)
values  (1, 'A101', 30),
    (2, 'B202', 20),
    (3, 'C303', 50),
    (4, 'Amphi 1', 200);

-- Insertion des cours
insert into cours (id, nom, debut, duree, professeur_id, type_cours)
values  
    (1, 'Java Basics', '2025-06-20 09:00:00', 120, 3, 'PRESENTIEL'),
    (2, 'Database Design', '2025-06-21 14:00:00', 90, 4, 'PRESENTIEL'),
    (3, 'Web Development', '2025-06-22 10:00:00', 180, 3, 'DISTANCIEL');

-- Relations entre cours et étudiants
insert into cours_etudiant (cours_id, etudiant_id)
values  (1, 1),
    (1, 2),
    (2, 1),
    (3, 2);

-- Données pour les cours présentiels
insert into presentiel (id, salle_id)
values  (1, 1),
    (2, 3);

-- Données pour les cours distanciels
insert into distanciel (id, lien_reunion)
values  (3, 'https://teams.microsoft.com/l/meetup-join/19%3ameeting_example');