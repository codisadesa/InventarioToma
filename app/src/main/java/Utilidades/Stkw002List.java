package Utilidades;

public class Stkw002List {
     private String nroToma;
    private String fechaToma;
    private String familia;
    private String grupo;
    private String area;
    private String dpto;
    private String tipoToma;
    private String seccion;

    public Stkw002List(String nroToma, String fechaToma, String familia, String grupo, String area, String dpto, String tipoToma, String seccion) {

        this.nroToma = nroToma;
        this.fechaToma = fechaToma;
        this.familia = familia;
        this.grupo = grupo;
        this.area = area;
        this.dpto = dpto;
        this.tipoToma = tipoToma;
        this.seccion = seccion;
     }
    public Stkw002List(){

    }
    public String getNroToma() {
        return this.nroToma;
    }
    public void setNroToma(String nroToma) {
        this.nroToma = nroToma;
    }

    public String getArea() {
        return this.area;
    }
    public void setArea(String area) {
        this.area = area;
    }


    public String getFechaToma() {
        return this.fechaToma;
    }
    public void setFechaToma(String fechaToma) {
        this.fechaToma = fechaToma;
    }

    public String getFamilia() {
        return this.familia;
    }
    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getGrupo() {
        return this.grupo;
    }
    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }


    public String getDpto() {
        return this.dpto;
    }
    public void setDpto(String dpto) {
        this.dpto = dpto;
    }


    public String getTipoToma() {
        return this.tipoToma;
    }
    public void setTipoToma(String tipoToma) {
        this.tipoToma = tipoToma;
    }


    public String getSeccion() {
        return this.seccion;
    }
    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }


}