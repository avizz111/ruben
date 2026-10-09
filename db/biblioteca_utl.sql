-- ============================================================
-- SCRIPT DE BASE DE DATOS: biblioteca_utl
-- UNIVERSIDAD TECNOLÓGICA DE LEÓN (UTL)
-- Materia: Programación Orientada a Objetos
-- Examen Extraordinario Oportunidad 2 - Unidad 3 y 4
-- ============================================================

CREATE DATABASE IF NOT EXISTS biblioteca_utl CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE biblioteca_utl;

-- 1. Tabla de Autores
CREATE TABLE IF NOT EXISTS autor (
    idAutor INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    PRIMARY KEY (idAutor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Tabla de Editoriales
CREATE TABLE IF NOT EXISTS editorial (
    idEditorial INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    PRIMARY KEY (idEditorial)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabla de Géneros Literarios
CREATE TABLE IF NOT EXISTS genero (
    idGenero INT(11) NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    PRIMARY KEY (idGenero)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabla Principal: libro (Diseño especificado en la práctica)
CREATE TABLE IF NOT EXISTS libro (
    idLibro INT(11) NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    idAutor INT(11) NOT NULL,
    idEditorial INT(11) NOT NULL,
    idGenero INT(11) NOT NULL,
    isbn VARCHAR(45) NOT NULL,
    foto LONGTEXT NULL,
    estatus INT(11) NOT NULL DEFAULT 1,
    PRIMARY KEY (idLibro),
    KEY fk_libro_autor (idAutor),
    KEY fk_libro_editorial (idEditorial),
    KEY fk_libro_genero (idGenero),
    CONSTRAINT fk_libro_autor FOREIGN KEY (idAutor) REFERENCES autor (idAutor) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_libro_editorial FOREIGN KEY (idEditorial) REFERENCES editorial (idEditorial) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_libro_genero FOREIGN KEY (idGenero) REFERENCES genero (idGenero) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- DATOS SEMILLA / CATÁLOGOS INICIALES
-- ============================================================

INSERT INTO autor (idAutor, nombre) VALUES
(1, 'Gabriel García Márquez'),
(2, 'George Orwell'),
(3, 'J.K. Rowling'),
(4, 'Miguel de Cervantes Saavedra'),
(5, 'Stephen King'),
(6, 'Mario Vargas Llosa')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO editorial (idEditorial, nombre) VALUES
(1, 'Penguin Random House'),
(2, 'Grupo Planeta'),
(3, 'Editorial Alfaguara'),
(4, 'Fondo de Cultura Económica'),
(5, 'Editorial Anagrama'),
(6, 'Alianza Editorial')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO genero (idGenero, nombre) VALUES
(1, 'Realismo Mágico'),
(2, 'Ciencia Ficción / Distopía'),
(3, 'Fantasía Juvenil'),
(4, 'Novela Clásica'),
(5, 'Terror / Suspenso'),
(6, 'Ensayo Histórico')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ============================================================
-- LIBROS DE EJEMPLO
-- ============================================================

INSERT INTO libro (idLibro, titulo, idAutor, idEditorial, idGenero, isbn, foto, estatus) VALUES
(1, 'Cien años de soledad', 1, 1, 1, '978-0307474728', '', 1),
(2, '1984', 2, 6, 2, '978-0451524935', '', 1),
(3, 'Harry Potter y la piedra filosofal', 3, 2, 3, '978-8478884452', '', 1),
(4, 'Don Quijote de la Mancha', 4, 4, 4, '978-8420412146', '', 1),
(5, 'El resplandor', 5, 1, 5, '978-0307743657', '', 1),
(6, 'Crónica de una muerte anunciada (Descatalogado)', 1, 3, 1, '978-1400034710', '', 0)
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo);
