package cl.untec.biblioteca.model;

/** Datos de usuario utilizados por DAO y vistas. */
public class Usuario implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private long id;

  public long getId() {
    return id;
  }

  public void setId(long value) {
    this.id = value;
  }

  private String nombre;

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String value) {
    this.nombre = value;
  }

  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  private String rol;

  public String getRol() {
    return rol;
  }

  public void setRol(String value) {
    this.rol = value;
  }
}
