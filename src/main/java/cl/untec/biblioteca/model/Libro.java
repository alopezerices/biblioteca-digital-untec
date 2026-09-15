package cl.untec.biblioteca.model;

/** Datos de libro utilizados por DAO y vistas. */
public class Libro implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long value) {
    this.id = value;
  }

  private String titulo;

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String value) {
    this.titulo = value;
  }

  private String autor;

  public String getAutor() {
    return autor;
  }

  public void setAutor(String value) {
    this.autor = value;
  }

  private String isbn;

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String value) {
    this.isbn = value;
  }

  private int ejemplares;

  public int getEjemplares() {
    return ejemplares;
  }

  public void setEjemplares(int value) {
    this.ejemplares = value;
  }

  private int disponibles;

  public int getDisponibles() {
    return disponibles;
  }

  public void setDisponibles(int value) {
    this.disponibles = value;
  }
}
