-- Ajouter la colonne type_cours à la table cours
ALTER TABLE cours ADD COLUMN type_cours VARCHAR(50) NULL;

-- Ajouter les colonnes de date et durée à la table cours 
ALTER TABLE cours ADD COLUMN debut DATETIME NULL;
ALTER TABLE cours ADD COLUMN duree INT NULL;

-- Création de la table salle
CREATE TABLE salle (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    capacite INT NOT NULL
);

-- Création de la table presentiel (sous-classe de cours)
CREATE TABLE presentiel (
    id INT PRIMARY KEY,
    salle_id INT,
    FOREIGN KEY (id) REFERENCES cours(id) ON DELETE CASCADE,
    FOREIGN KEY (salle_id) REFERENCES salle(id)
);

-- Création de la table distanciel (sous-classe de cours)
CREATE TABLE distanciel (
    id INT PRIMARY KEY,
    lien_reunion VARCHAR(500),
    FOREIGN KEY (id) REFERENCES cours(id) ON DELETE CASCADE
);

-- Données initiales pour les salles
INSERT INTO salle (nom, capacite) VALUES 
    ('A101', 30),
    ('B202', 20),
    ('C303', 50),
    ('Amphi 1', 200);

-- Données initiales pour les administrateurs
INSERT INTO utilisateur (nom_role, email, password) VALUES
    ('ADMINISTRATEUR', 'admin@example.com', 'admin123');

-- Mise à jour des cours existants en définissant type_cours = 'PRESENTIEL' par défaut
-- et affectation des salles aléatoirement pour la démonstration
UPDATE cours SET type_cours = 'PRESENTIEL', debut = NOW(), duree = 90;
-- Pour chaque cours existant, créer une entrée dans la table presentiel
INSERT INTO presentiel (id, salle_id)
SELECT c.id, 1 FROM cours c;
