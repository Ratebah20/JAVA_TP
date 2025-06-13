insert into utilisateur (id, email, password, nom_role)
values  (1, 'a@a.com', 'root', 'ETUDIANT'),
    (2, 'b@b.com', 'root', 'ETUDIANT'),
    (3, 'c@c.com', 'root', 'PROFESSEUR'),
    (4, 'd@d.com', 'root', 'PROFESSEUR');

insert into etudiant (date_naissance, id)
values  ('2000-05-27', 1),
    ('2001-05-20', 2);

insert into professeur (annees_experience, id)
values  (15, 3),
    (20, 4);