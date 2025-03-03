package edu.pucmm.icc352.modelo;


import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class Comentario implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String comentario;
    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autorId;
    @ManyToOne
    @JoinColumn(name = "articulo_id", nullable = false)
    private Articulo articulo;

    public Comentario( String comentario, Usuario autorId, Articulo articulo) {
        this.comentario = comentario;
        this.autorId = autorId;
        this.articulo = articulo;

    }

    public Comentario() {

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
