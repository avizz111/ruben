package controller;

import model.Autor;
import model.ConexionBD;
import model.Editorial;
import model.Genero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la obtención de catálogos relacionales (Autor, Editorial, Género).
 * Facilita el llenado de componentes gráficos (combos) manteniendo la arquitectura MVC.
 */
public class CatalogoController {

    public List<Autor> obtenerAutores() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT idAutor, nombre FROM autor ORDER BY nombre ASC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Autor(rs.getInt("idAutor"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar autores: " + e.getMessage());
        }
        return lista;
    }

    public List<Editorial> obtenerEditoriales() {
        List<Editorial> lista = new ArrayList<>();
        String sql = "SELECT idEditorial, nombre FROM editorial ORDER BY nombre ASC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Editorial(rs.getInt("idEditorial"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar editoriales: " + e.getMessage());
        }
        return lista;
    }

    public List<Genero> obtenerGeneros() {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT idGenero, nombre FROM genero ORDER BY nombre ASC";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Genero(rs.getInt("idGenero"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar géneros: " + e.getMessage());
        }
        return lista;
    }
}
