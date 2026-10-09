package controller;

import model.ConexionBD;
import model.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la entidad Libro (Capa Controlador en MVC).
 *
 * Cumple con el criterio de evaluación:
 * "el controlador debe recibir como parámetros, objetos del tipo tu modelo,
 * realizar los cálculos/operaciones y devolver un resultado."
 */
public class LibroController {

    /**
     * Inserta un nuevo libro en la base de datos MySQL.
     * @param libro Objeto del modelo Libro con los datos capturados en la vista.
     * @return true si se insertó con éxito, false en caso contrario.
     */
    public boolean insertar(Libro libro) {
        if (libro == null) {
            System.err.println("Error: El objeto libro recibido es nulo.");
            return false;
        }

        // Validación de datos requeridos del modelo
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            System.err.println("Validación: El título del libro no puede estar vacío.");
            return false;
        }
        if (libro.getIsbn() == null || libro.getIsbn().trim().isEmpty()) {
            System.err.println("Validación: El ISBN del libro no puede estar vacío.");
            return false;
        }
        if (libro.getIdAutor() <= 0 || libro.getIdEditorial() <= 0 || libro.getIdGenero() <= 0) {
            System.err.println("Validación: Debe seleccionar un autor, editorial y género válidos.");
            return false;
        }

        String sql = "INSERT INTO libro (titulo, idAutor, idEditorial, idGenero, isbn, foto, estatus) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libro.getTitulo().trim());
            ps.setInt(2, libro.getIdAutor());
            ps.setInt(3, libro.getIdEditorial());
            ps.setInt(4, libro.getIdGenero());
            ps.setString(5, libro.getIsbn().trim());
            ps.setString(6, libro.getFoto() != null ? libro.getFoto() : "");
            ps.setInt(7, libro.getEstatus());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        libro.setIdLibro(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al insertar libro: " + e.getMessage());
        }
        return false;
    }

    /**
     * Actualiza la información de un libro existente.
     * @param libro Objeto del modelo Libro con los datos actualizados.
     * @return true si se actualizó correctamente, false en caso contrario.
     */
    public boolean actualizar(Libro libro) {
        if (libro == null || libro.getIdLibro() <= 0) {
            System.err.println("Error: Objeto libro inválido o ID no especificado.");
            return false;
        }

        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            System.err.println("Validación: El título no puede estar vacío.");
            return false;
        }

        String sql = "UPDATE libro SET titulo = ?, idAutor = ?, idEditorial = ?, idGenero = ?, isbn = ?, foto = ?, estatus = ? WHERE idLibro = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo().trim());
            ps.setInt(2, libro.getIdAutor());
            ps.setInt(3, libro.getIdEditorial());
            ps.setInt(4, libro.getIdGenero());
            ps.setString(5, libro.getIsbn().trim());
            ps.setString(6, libro.getFoto() != null ? libro.getFoto() : "");
            ps.setInt(7, libro.getEstatus());
            ps.setInt(8, libro.getIdLibro());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar libro: " + e.getMessage());
        }
        return false;
    }

    /**
     * Realiza la eliminación lógica del libro estableciendo su estatus en 0.
     * No se borra físicamente el registro de la base de datos para preservar integridad.
     * @param libro Objeto del modelo Libro que se desea eliminar lógicamente.
     * @return true si se realizó la eliminación lógica, false en caso contrario.
     */
    public boolean eliminarLogico(Libro libro) {
        if (libro == null || libro.getIdLibro() <= 0) {
            System.err.println("Error: Objeto libro inválido para eliminación lógica.");
            return false;
        }

        String sql = "UPDATE libro SET estatus = 0 WHERE idLibro = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, libro.getIdLibro());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                libro.setEstatus(0);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar lógicamente el libro: " + e.getMessage());
        }
        return false;
    }

    /**
     * Reactiva un libro previamente eliminado lógicamente (estatus = 1).
     * @param libro Objeto del modelo Libro.
     * @return true si se reactivó correctamente.
     */
    public boolean reactivar(Libro libro) {
        if (libro == null || libro.getIdLibro() <= 0) {
            return false;
        }

        String sql = "UPDATE libro SET estatus = 1 WHERE idLibro = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, libro.getIdLibro());
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                libro.setEstatus(1);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al reactivar libro: " + e.getMessage());
        }
        return false;
    }

    /**
     * Consulta y devuelve la lista de libros registrados.
     * Realiza un JOIN relacional para obtener los nombres de autor, editorial y género.
     * @param soloActivos true para filtrar únicamente libros activos (estatus = 1), false para todos.
     * @return Lista de objetos de tipo Libro.
     */
    public List<Libro> consultarTodos(boolean soloActivos) {
        List<Libro> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.idLibro, l.titulo, l.idAutor, a.nombre AS autorNombre, ")
           .append("l.idEditorial, e.nombre AS editorialNombre, ")
           .append("l.idGenero, g.nombre AS generoNombre, ")
           .append("l.isbn, l.foto, l.estatus ")
           .append("FROM libro l ")
           .append("LEFT JOIN autor a ON l.idAutor = a.idAutor ")
           .append("LEFT JOIN editorial e ON l.idEditorial = e.idEditorial ")
           .append("LEFT JOIN genero g ON l.idGenero = g.idGenero ");

        if (soloActivos) {
            sql.append("WHERE l.estatus = 1 ");
        }
        sql.append("ORDER BY l.idLibro ASC");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro lib = mapearLibro(rs);
                lista.add(lib);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al consultar libros: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca libros por título, ISBN o nombre del autor.
     * @param criterio Texto a buscar.
     * @param soloActivos true para solo activos.
     * @return Lista de libros coincidentes.
     */
    public List<Libro> buscar(String criterio, boolean soloActivos) {
        List<Libro> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.idLibro, l.titulo, l.idAutor, a.nombre AS autorNombre, ")
           .append("l.idEditorial, e.nombre AS editorialNombre, ")
           .append("l.idGenero, g.nombre AS generoNombre, ")
           .append("l.isbn, l.foto, l.estatus ")
           .append("FROM libro l ")
           .append("LEFT JOIN autor a ON l.idAutor = a.idAutor ")
           .append("LEFT JOIN editorial e ON l.idEditorial = e.idEditorial ")
           .append("LEFT JOIN genero g ON l.idGenero = g.idGenero ")
           .append("WHERE (l.titulo LIKE ? OR l.isbn LIKE ? OR a.nombre LIKE ?) ");

        if (soloActivos) {
            sql.append("AND l.estatus = 1 ");
        }
        sql.append("ORDER BY l.idLibro ASC");

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            String term = "%" + (criterio != null ? criterio.trim() : "") + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            ps.setString(3, term);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al buscar libros: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Consulta un libro específico por su ID.
     * @param idLibro ID a consultar.
     * @return Objeto Libro o null si no se encuentra.
     */
    public Libro consultarPorId(int idLibro) {
        String sql = "SELECT l.idLibro, l.titulo, l.idAutor, a.nombre AS autorNombre, " +
                     "l.idEditorial, e.nombre AS editorialNombre, " +
                     "l.idGenero, g.nombre AS generoNombre, " +
                     "l.isbn, l.foto, l.estatus " +
                     "FROM libro l " +
                     "LEFT JOIN autor a ON l.idAutor = a.idAutor " +
                     "LEFT JOIN editorial e ON l.idEditorial = e.idEditorial " +
                     "LEFT JOIN genero g ON l.idGenero = g.idGenero " +
                     "WHERE l.idLibro = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al consultar libro por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Mapea un ResultSet de MySQL a una instancia del modelo Libro.
     */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setIdLibro(rs.getInt("idLibro"));
        l.setTitulo(rs.getString("titulo"));
        l.setIdAutor(rs.getInt("idAutor"));
        l.setNombreAutor(rs.getString("autorNombre") != null ? rs.getString("autorNombre") : "ID " + l.getIdAutor());
        l.setIdEditorial(rs.getInt("idEditorial"));
        l.setNombreEditorial(rs.getString("editorialNombre") != null ? rs.getString("editorialNombre") : "ID " + l.getIdEditorial());
        l.setIdGenero(rs.getInt("idGenero"));
        l.setNombreGenero(rs.getString("generoNombre") != null ? rs.getString("generoNombre") : "ID " + l.getIdGenero());
        l.setIsbn(rs.getString("isbn"));
        l.setFoto(rs.getString("foto"));
        l.setEstatus(rs.getInt("estatus"));
        return l;
    }
}
