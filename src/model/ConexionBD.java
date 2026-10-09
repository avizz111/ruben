package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestor de conexión JDBC con MySQL.
 * Lee la configuración desde config.properties si existe,
 * o utiliza los valores predeterminados para el entorno local.
 */
public class ConexionBD {
    private static String host = "localhost";
    private static String port = "3306";
    private static String database = "biblioteca_utl";
    private static String user = "root";
    private static String password = "1234";

    static {
        cargarConfiguracion();
    }

    private static void cargarConfiguracion() {
        File configFile = new File("config.properties");
        if (configFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(configFile)) {
                props.load(fis);
                host = props.getProperty("db.host", host);
                port = props.getProperty("db.port", port);
                database = props.getProperty("db.name", database);
                user = props.getProperty("db.user", user);
                password = props.getProperty("db.password", password);
            } catch (IOException e) {
                System.err.println("Advertencia: No se pudo leer config.properties, usando valores por defecto: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Controlador MySQL JDBC no encontrado en el classpath.", e);
        }

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, database);

        return DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Fallo prueba de conexión MySQL: " + e.getMessage());
            return false;
        }
    }

    public static String getDatabaseName() {
        return database;
    }

    public static String getUser() {
        return user;
    }
}
