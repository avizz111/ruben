package model;

/**
 * Modelo para representar la entidad Autor.
 */
public class Autor {
    private int idAutor;
    private String nombre;

    public Autor() {
    }

    public Autor(int idAutor, String nombre) {
        this.idAutor = idAutor;
        this.nombre = nombre;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return idAutor + " - " + nombre;
    }
}
