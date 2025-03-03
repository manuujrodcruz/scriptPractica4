package edu.pucmm.icc352.modelo;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Articulo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false,length = 25000)
    private String cuerpo;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false)
    private Date fecha;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Comentario> listaComentario = new ArrayList<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "articulo_etiqueta",
            joinColumns = @JoinColumn(name = "articulo_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    private Set<Etiqueta> listaEtiqueta = new HashSet<>();

    // Constructor vacío...
    public Articulo() {}

    public Articulo(String titulo, String cuerpo, Usuario autor, Date fecha,
                    ArrayList<Comentario> listaComentario, Set<Etiqueta> listaEtiqueta) {
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

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public Set<Etiqueta> getListaEtiqueta() {
        return listaEtiqueta;
    }

    public void setListaEtiqueta(Set<Etiqueta> listaEtiqueta) {
        this.listaEtiqueta = listaEtiqueta;
    }

    public void agregarEtiqueta(Etiqueta etiqueta) {
        this.listaEtiqueta.add(etiqueta);
    }

    public String obtenerResumen(Articulo articulo) {
        if(articulo.getCuerpo().length() > 70){
            return articulo.getCuerpo().substring(0, 70) + "...";
        }
        return articulo.getCuerpo();
    }
    public String obtenerEtiquetas(Articulo articulo) {
        StringBuilder resultado = new StringBuilder();
        for (Etiqueta etiqueta : articulo.getListaEtiqueta()) {
            if (!resultado.isEmpty()) {
                resultado.append(", ");
            }
            resultado.append(etiqueta.getEtiqueta());
        }
        return resultado.toString();
    }

}