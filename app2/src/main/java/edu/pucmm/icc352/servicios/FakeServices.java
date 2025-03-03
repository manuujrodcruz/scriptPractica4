package edu.pucmm.icc352.servicios;

import edu.pucmm.icc352.modelo.Articulo;
import edu.pucmm.icc352.modelo.Comentario;
import edu.pucmm.icc352.modelo.Etiqueta;
import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.util.RolesApp;

import java.util.*;

public class FakeServices {
    private static FakeServices instancia;

    //colecciones
    private List<Etiqueta> listaEtiqueta = new ArrayList<>();
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private List<Comentario> listaComentarios = new ArrayList<>();
    private List<Articulo> listaArticulos = new ArrayList<>();

    /**
     * Constructor privado.
     */
    private FakeServices() {
        /// Agregando el usuario admin
        listaUsuarios.add(new Usuario("Admin", "Admin", "12345", Set.of(RolesApp.ROLE_ADMIN, RolesApp.CUALQUIERA, RolesApp.ROLE_AUTOR)));

        // Agregando etiquetas
        listaEtiqueta.add(new Etiqueta(1, "java"));

        // Creamos un artículo para añadirlo a la lista
        Articulo articulo = new Articulo(1, "Primer Artículo", "Contenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículoContenido del artículo", getUsuarioByUsername("Admin"), new Date(), new ArrayList<>(), new ArrayList<>(List.of(getEtiquetaByID(1))));
        listaArticulos.add(articulo);

        // Añadimos un comentario asociado al artículo creado
        listaComentarios.add(new Comentario(1, "muy bueno", getUsuarioByUsername("Admin"), articulo));
        articulo.getListaComentario().add(listaComentarios.get(0));
        // Agregando más artículos
        Articulo articulo2 = new Articulo(2, "Segundo Artículo", "Contenido del segundo artículo", getUsuarioByUsername("Admin"), new Date(), new ArrayList<>(), new ArrayList<>(List.of(getEtiquetaByID(1))));
        listaArticulos.add(articulo2);


    }

    public static FakeServices getInstancia() {
        if (instancia == null) {
            instancia = new FakeServices();
        }
        return instancia;
    }

    /* ------- GETTERs -------- */
    public List<Usuario> getListaUsuarios() {
        return listaUsuarios;
    }

    public List<Etiqueta> getListaEtiqueta() {
        return listaEtiqueta;
    }

    public List<Comentario> getListaComentarios() {
        return listaComentarios;
    }

    public List<Articulo> getListaArticulos() {
        return listaArticulos;
    }


    /* ---- Buscar por IDs ---- */
    public Usuario getUsuarioByUsername(String username) {
        return listaUsuarios.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    public Articulo getArticuloById(long id) {
        return listaArticulos.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public Comentario getComentarioByID(long id) {
        return listaComentarios.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public Etiqueta getEtiquetaByID(long id) {
        return listaEtiqueta.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
    }

    /* ---- C R E A R ----*/
    public Articulo crearArticulo(Articulo articulo) {
        int id = listaArticulos.size() + 1;
        articulo.setId(id);
        listaArticulos.add(articulo);
        return articulo;
    }

    public Comentario crearComentario(Comentario comentario) {
        int id = listaComentarios.size() + 1;
        comentario.setId(id);
        listaComentarios.add(comentario);
        return comentario;
    }

    public Etiqueta crearEtiqueta(String etiqueta) {
        int id = listaEtiqueta.size() + 1;
        Etiqueta etiquetaObj = new Etiqueta(id, etiqueta);
        listaEtiqueta.add(etiquetaObj);
        return etiquetaObj;
    }

    public void actualizarArticulo(Articulo articulo) {
        Articulo articuloTmp = getArticuloById(articulo.getId());
        articuloTmp.setTitulo(articulo.getTitulo());
        articuloTmp.setCuerpo(articulo.getCuerpo());
        articuloTmp.setFecha(new Date());
        articuloTmp.setListaComentario(articulo.getListaComentario());
        articuloTmp.setListaEtiqueta(articulo.getListaEtiqueta());
        articuloTmp.setAutor(articulo.getAutor());
        listaArticulos.set(listaArticulos.indexOf(articuloTmp), articuloTmp);
    }

    public void eliminarArticulo(long id) {
        Articulo articuloTmp = getArticuloById(id);
        if (articuloTmp != null) {
            listaComentarios.removeIf(comentario -> comentario.getArticulo().equals(articuloTmp));
            listaArticulos.remove(articuloTmp);
          // Ahora, verificamos las etiquetas asociadas al artículo eliminado
            List<Etiqueta> etiquetas = articuloTmp.getListaEtiqueta();
            for (Etiqueta etiqueta : etiquetas) {
                // Verificamos si la etiqueta está asociada a otros artículos
                boolean estaAsociada = listaArticulos.stream()
                        .anyMatch(a -> a.getListaEtiqueta().contains(etiqueta));

                // Si no está asociada a ningún otro artículo, la eliminamos
                if (!estaAsociada) {
                    listaEtiqueta.remove(etiqueta);
                }
            }
        }
    }


    public void eliminarComentario(String idComentario) {
        long id = Long.parseLong(idComentario);

        for (Articulo articulo : listaArticulos) {
            Comentario comentarioAEliminar = null;

            for (Comentario comentario : articulo.getListaComentario()) {
                if (comentario.getId() == id) {
                    comentarioAEliminar = comentario;
                    break;
                }
            }

            if (comentarioAEliminar != null) {
                articulo.getListaComentario().remove(comentarioAEliminar);
                System.out.println("Comentario eliminado con éxito: " + id);
                return;
            }
        }

        System.out.println("Comentario no encontrado con ID: " + id);
    }




    public List<Articulo> getListaArticulosOrdenada(List<Articulo> listaArticulos) {
        // Ordenar los artículos alfabéticamente por su fecha
        listaArticulos.sort(Comparator.comparing(Articulo::getFecha).reversed());
        return listaArticulos;
    }


    public void eliminarUsuario(Usuario usuario) {
        if (usuario != null) {
            listaUsuarios.remove(usuario);
        } else {
            System.out.println("Usuario no puede ser nulo.");
        }
    }

    public void actualizarUsuario(Usuario usuario) {
        Usuario usuarioExistente = getUsuarioByUsername(usuario.getUsername());
        if (usuarioExistente != null) {
            // Actualiza los atributos del usuario existente
            usuarioExistente.setUsername(usuario.getUsername());
            usuarioExistente.setNombre(usuario.getNombre());
            usuarioExistente.setPassword(usuario.getPassword());
            usuarioExistente.setListaRoles(usuario.getListaRoles());
        } else {
            System.out.println("Usuario no encontrado en la lista.");
        }
    }

}

