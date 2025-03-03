package edu.pucmm.icc352.modelo;


import java. util. Date;
import java. util. ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Articulo {
    private long id;
    private String titulo;
    private String cuerpo;
    private Usuario autor;
    private Date fecha;
    private List<Comentario> listaComentario;
    private List<Etiqueta> listaEtiqueta;

    public Articulo(long id, String titulo, String cuerpo, Usuario autor, Date fecha,
                    ArrayList<Comentario> listaComentario, List<Etiqueta> listaEtiqueta) {
        this.id = id;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.autor = autor;
        this.fecha = fecha;
        this.listaComentario = listaComentario;
        this.listaEtiqueta = listaEtiqueta;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getCuerpo() {
        return cuerpo;
    }
    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }
    public Usuario getAutor() {
        return autor;
    }
    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public List<Comentario> getListaComentario() {
        return listaComentario;
    }
    public void setListaComentario(List<Comentario> listaComentario) {
        this.listaComentario = listaComentario;
    }
    public List<Etiqueta> getListaEtiqueta() {
        return listaEtiqueta;
    }
    public void setListaEtiqueta(List<Etiqueta> listaEtiqueta) {
        this.listaEtiqueta = listaEtiqueta;
    }


    /* ---- UTILES ---- */
    public String obtenerEtiquetasComoCadena() {
        return listaEtiqueta.stream()
                .map(Etiqueta::getEtiqueta)
                .distinct()
                .collect(Collectors.joining(", "));
    }
    public String obtenerResumen(String contenido) {
        return contenido.length() > 70 ? contenido.substring(0, 70) + "..." : contenido;
    }
    public String obtenerEtiquetas(Articulo articulo) {
        if (articulo.getListaEtiqueta() == null || articulo.getListaEtiqueta().isEmpty()) {
            return "";
        }
        return articulo.getListaEtiqueta().stream()
                .map(Etiqueta::getEtiqueta)
                .collect(Collectors.joining(", "));
    }




}
