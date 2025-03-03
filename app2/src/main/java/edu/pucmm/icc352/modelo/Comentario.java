package edu.pucmm.icc352.modelo;



public class Comentario {
    private int id;
    private String comentario;
    private Usuario autorId;
    private Articulo articulo;

    public Comentario(int id, String comentario, Usuario autorId, Articulo articulo) {
        this.id = id;
        this.comentario = comentario;
        this.autorId = autorId;
        this.articulo = articulo;

    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public Usuario getAutorId() {
        return autorId;
    }
    public void setAutorId(Usuario autorId) {
        this.autorId = autorId;
    }
    public Articulo getArticulo() {
        return articulo;
    }
    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }
}
