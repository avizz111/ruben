# Sistema de Gestión de Biblioteca (Patrón MVC)

**Universidad Tecnológica de León (UTL)**  
**Carrera:** Ingeniería en Tecnologías de la Información e Innovación Digital  
**Materia:** Programación Orientada a Objetos  
**Examen Extraordinario Oportunidad 2 - Actividades (Unidades 3 y 4)**

---

## 📋 Descripción del Proyecto

Aplicación con interfaz gráfica desarrollada en **Java (Swing)** y conectada a una base de datos **MySQL**, implementando de forma estricta el patrón arquitectónico **Modelo - Vista - Controlador (MVC)**.

Cumple con todos los requerimientos especificados en la evaluación:
1. **Capa Modelo (`model`):** Clases que representan las entidades del sistema (`Libro`, `Autor`, `Editorial`, `Genero`) y la conexión a MySQL (`ConexionBD`).
2. **Capa Controlador (`controller`):** Recibe como parámetros objetos del tipo del modelo, aplica reglas de validación y ejecuta las operaciones CRUD en la base de datos MySQL devolviendo resultados claros.
3. **Capa Vista (`view`):** Interfaz gráfica intuitiva con tabla interactiva, formulario con campos validados, selector y previsualización de imágenes (Base64 / `LONGTEXT`), filtros y buscador en vivo.
4. **Operaciones requeridas:**
   - **Inserción:** Registro de nuevos libros con auto-incremento de ID.
   - **Consulta:** Búsqueda en vivo por título, ISBN o autor, y filtro para visualizar sólo activos o el historial completo.
   - **Actualización:** Modificación de datos y portada de un libro seleccionado.
   - **Eliminación Lógica:** Cambia el `estatus` a `0` sin borrar físicamente el registro, preservando la integridad de la base de datos (con opción de reactivar a `1`).

---

## 🗄️ Diseño Relacional de la Base de Datos

### Tabla: `libro`
| Campo | Tipo | Clave / Restricción | Descripción |
|---|---|---|---|
| `idLibro` | `INT(11)` | PRIMARY KEY AUTO_INCREMENT | Identificador único del libro |
| `titulo` | `VARCHAR(200)` | NOT NULL | Título de la obra |
| `idAutor` | `INT(11)` | FOREIGN KEY -> `autor(idAutor)` | ID del autor |
| `idEditorial` | `INT(11)` | FOREIGN KEY -> `editorial(idEditorial)` | ID de la editorial |
| `idGenero` | `INT(11)` | FOREIGN KEY -> `genero(idGenero)` | ID del género literario |
| `isbn` | `VARCHAR(45)` | NOT NULL | Código internacional normalizado |
| `foto` | `LONGTEXT` | NULL | Imagen de portada codificada en Base64 |
| `estatus` | `INT(11)` | NOT NULL DEFAULT 1 | `1` = Activo, `0` = Inactivo (Eliminado Lógico) |

Tablas de catálogo complementarias para asegurar la integridad relacional:
- `autor (idAutor, nombre)`
- `editorial (idEditorial, nombre)`
- `genero (idGenero, nombre)`

---

## 📁 Estructura del Proyecto

```text
ruben/
├── .gitignore                      # Archivos ignorados por git
├── README.md                       # Documentación del proyecto
├── compilar.bat                    # Script para compilar el código Java
├── ejecutar.bat                    # Script para ejecutar la aplicación
├── config.properties               # Configuración de conexión MySQL (host, puerto, usuario, contraseña)
├── db/
│   └── biblioteca_utl.sql          # Script SQL con DDL y datos semilla
├── lib/
│   └── mysql-connector-j-8.4.0.jar # Driver JDBC oficial de MySQL
├── bin/                            # Archivos .class compilados
└── src/
    ├── Main.java                   # Punto de inicio de la aplicación
    ├── model/                      # Capa Modelo (POJOs y Conexión)
    │   ├── Autor.java
    │   ├── Editorial.java
    │   ├── Genero.java
    │   ├── Libro.java
    │   └── ConexionBD.java
    ├── controller/                 # Capa Controlador (Lógica de negocio y persistencia)
    │   ├── CatalogoController.java
    │   └── LibroController.java
    └── view/                       # Capa Vista (GUI Swing)
        ├── ImageUtil.java
        └── VentanaPrincipal.java
```

---

## 🚀 Instrucciones de Instalación y Ejecución

### 1. Configurar la Base de Datos MySQL
El script de base de datos se encuentra en `db/biblioteca_utl.sql`. Para crearlo e importar los datos de ejemplo:
- Abre **MySQL Workbench** o la consola de comandos de MySQL y ejecuta:
  ```sql
  source c:/Users/Ing. Ismael Avila/Desktop/ruben/db/biblioteca_utl.sql;
  ```
- O desde la consola de Windows:
  ```bash
  mysql -u root -p < db/biblioteca_utl.sql
  ```

### 2. Configurar Credenciales (`config.properties`)
Si tus credenciales de MySQL difieren de las estándar, puedes editarlas en el archivo `config.properties`:
```properties
db.host=localhost
db.port=3306
db.name=biblioteca_utl
db.user=root
db.password=1234
```

### 3. Compilar y Ejecutar
- **Método 1 (Fácil con doble clic):**
  - Haz doble clic en `compilar.bat` para compilar los fuentes.
  - Haz doble clic en `ejecutar.bat` para iniciar el programa.
- **Método 2 (Línea de comandos):**
  ```bash
  javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.4.0.jar;src" -d bin src/model/*.java src/controller/*.java src/view/*.java src/Main.java
  java -cp "bin;lib/mysql-connector-j-8.4.0.jar" Main
  ```

---

## 📤 Unidad 4: Subida al Repositorio Privado de GitHub

Para cumplir con el requerimiento de la **Unidad 4**:
1. Abre una terminal dentro de esta carpeta (`c:\Users\Ing. Ismael Avila\Desktop\ruben`).
2. Inicializa el repositorio Git:
   ```bash
   git init
   git add .
   git commit -m "Entrega Examen POO - Unidades 3 y 4 (Sistema Biblioteca MVC)"
   ```
3. Crea un repositorio **privado** en tu cuenta de GitHub (ejemplo: `examen-poo-biblioteca`).
4. Vincula y sube los cambios:
   ```bash
   git branch -M main
   git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
   git push -u origin main
   ```
