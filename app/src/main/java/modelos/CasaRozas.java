package modelos;

/**
 * Created by Susana on 14/11/2016.
 */

public class CasaRozas {

    private int id;
    private String lugar;
    private String anio;
    private String mes;
    private String imagen;
    private String descripcion;



  /*  public CasaRozas(int id, String lugar, String anio, String mes, String imagen, String descripcion) {
        this.id = id;
        this.lugar = lugar;
        this.anio = anio;
        this.mes = mes;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "CasaRozas{" +
                "id=" + id +
                ", lugar='" + lugar + '\'' +
                ", anio='" + anio + '\'' +
                ", mes='" + mes + '\'' +
                ", imagen='" + imagen + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
