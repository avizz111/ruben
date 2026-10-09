package model;

/**
 * Modelo para representar la entidad Libro según el diseño relacional:
 * - idLibro (INT(11))
 * - titulo (VARCHAR(200))
 * - idAutor (INT(11))
 * - idEditorial (INT(11))
 * - idGenero (INT(11))
 * - isbn (VARCHAR(45))
 * - foto (LONGTEXT)
 * - estatus (INT(11))
 */
public class Libro {
    private int idLibro;
    private String titulo;
    private int idAutor;
    private int idEditorial;
    private int idGenero;
    private String isbn;
    private String foto;
    private int estatus;

    // Atributos auxiliares para visualización en Vista / Tabla
    private String nombreAutor;
    private String nombreEditorial;
    private String nombreGenero;

    public Libro() {
        this.estatus = 1; // Por defecto activo
        this.foto = "";
    }

    public Libro(String titulo, int idAutor, int idEditorial, int idGenero, String isbn, String foto, int estatus) {
        this.titulo = titulo;
        this.idAutor = idAutor;
        this.idEditorial = idEditorial;
        this.idGenero = idGenero;
        this.isbn = isbn;
        this.foto = foto;
        this.estatus = estatus;
    }

    public Libro(int idLibro, String titulo, int idAutor, int idEditorial, int idGenero, String isbn, String foto, int estatus) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.idAutor = idAutor;
        this.idEditorial = idEditorial;
        this.idGenero = idGenero;
        this.isbn = isbn;
        this.foto = foto;
        this.estatus = estatus;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public int getIdEditorial() {
        return idEditorial;
    }

    public void setIdEditorial(int idEditorial) {
        this.idEditorial = idEditorial;
    }

    public int getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getNombreEditorial() {
        return nombreEditorial;
    }

    public void setNombreEditorial(String nombreEditorial) {
        this.nombreEditorial = nombreEditorial;
    }

    public String getNombreGenero() {
        return nombreGenero;
    }

    public void setNombreGenero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "idLibro=" + idLibro +
                ", titulo='" + titulo + '\'' +
                ", idAutor=" + idAutor +
                ", idEditorial=" + idEditorial +
                ", idGenero=" + idGenero +
                ", isbn='" + isbn + '\'' +
                ", estatus=" + estatus +
                '}';
    }
}
