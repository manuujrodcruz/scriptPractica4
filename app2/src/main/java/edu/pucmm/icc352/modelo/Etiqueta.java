package edu.pucmm.icc352.modelo;

public class Etiqueta {
    private long id;
    private String etiqueta;

    public Etiqueta(int id, String etiqueta) {
        this.id = id;
        this.etiqueta = etiqueta;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}

