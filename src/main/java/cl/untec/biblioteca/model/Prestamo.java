package cl.untec.biblioteca.model;

/** Datos de prestamo utilizados por DAO y vistas. */
public class Prestamo implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long value) {
    this.id = value;
  }

  private long usuarioId;

  public long getUsuarioId() {
    return usuarioId;
  }

  public void setUsuarioId(long value) {
    this.usuarioId = value;
  }

  private long libroId;

  public long getLibroId() {
    return libroId;
  }

  public void setLibroId(long value) {
    this.libroId = value;
  }

  private String usuario;

  public String getUsuario() {
    return usuario;
  }

  public void setUsuario(String value) {
    this.usuario = value;
  }

  private String libro;

  public String getLibro() {
    return libro;
  }

  public void setLibro(String value) {
    this.libro = value;
  }

  private String fecha;

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String value) {
    this.fecha = value;
  }

  private String vence;

  public String getVence() {
    return vence;
  }

  public void setVence(String value) {
    this.vence = value;
  }

  private String devolucion;

  public String getDevolucion() {
    return devolucion;
  }

  public void setDevolucion(String value) {
    this.devolucion = value;
  }
}
