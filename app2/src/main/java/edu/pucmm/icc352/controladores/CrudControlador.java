package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.modelo.Articulo;
import edu.pucmm.icc352.modelo.Comentario;
import edu.pucmm.icc352.modelo.Etiqueta;
import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.servicios.FakeServices;
import edu.pucmm.icc352.util.BaseControlador;
import edu.pucmm.icc352.util.RolesApp;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import io.javalin.Javalin;

import java.util.*;

public class CrudControlador extends BaseControlador {

    private final static FakeServices fakeServices = FakeServices.getInstancia();

    public CrudControlador(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {

        /*
         * Aplicando la configuracion para manejar los roles
         */

        app.beforeMatched("/*", ctx -> {
            // Si el endpoint no tiene roles asignados no es necesario verificarlo.
            if (ctx.routeRoles().isEmpty()) {
                return;
            }

            // Obtener el usuario de la sesión
            final Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null) {
                ctx.redirect("/login");
                return; // Salir si el usuario no está autenticado
            }

            // Listando los roles del endpoint.
            System.out.println("Los roles asignados a la ruta: " + ctx.routeRoles());

            // Buscando el permiso del usuario.
            Usuario usuarioTmp = fakeServices.getListaUsuarios().stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(usuario.getUsername()))
                    .findAny()
                    .orElse(null);

            if (usuarioTmp == null) {
                throw new UnauthorizedResponse("No tiene roles para acceder...");
            }

            System.out.println("Los roles asignados en el usuario: " + usuarioTmp.getListaRoles().toString());

            // Validando que el usuario registrado tiene el rol permitido.
            boolean encontrado = false;
            for (RolesApp role : usuarioTmp.getListaRoles()) {
                if (ctx.routeRoles().contains(role)) {
                    System.out.println(String.format("El Usuario: %s - con el Rol: %s tiene permiso", usuarioTmp.getUsername(), role.name()));
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                throw new UnauthorizedResponse("No tiene roles para acceder...");
            }
        });

        /*------------ DEFINICION DE RUTAS ------------*/

        /*---- Articulo ----*/
        app.get("/articulo/crear", ctx -> {
            // Obtener el usuario de la sesión
            Usuario usuario = ctx.sessionAttribute("usuario");

            // Verificar si el usuario está autenticado
            if (usuario == null) {
                ctx.redirect("/login"); // Redirigir a la página de inicio de sesión si no está autenticado
                return; // Asegúrate de salir del método después de redirigir
            }
            Articulo articulo = null;

            Map<String, Object> model = new HashMap<>();
            model.put("usuario", usuario);
            model.put("titulo", "Formulario de creación de Artículo");
            model.put("accion", "/articulo/crear/");
            model.put("articulo", articulo);
            ctx.render("/template/crud/articulo/crearEditarEliminar.html", model);
        }, RolesApp.ROLE_AUTOR, RolesApp.ROLE_ADMIN);

        app.post("/articulo/crear/", ctx -> {
            Articulo articulo = null;
            procesarArticulo(ctx, articulo);
        }, RolesApp.ROLE_AUTOR, RolesApp.ROLE_ADMIN);

        app.get("/articulo/editar/{id}", (Handler) ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = fakeServices.getArticuloById(id);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Formulario de Edicion de Articulo");
            model.put("action", "articulo/editar" + "/" + id);
            model.put("articulo", articulo);
            ctx.render("/template/crud/articulo/crearEditarEliminar.html", model);
        }, RolesApp.ROLE_ADMIN, RolesApp.ROLE_AUTOR);


        app.post("/articulo/editar/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = fakeServices.getArticuloById(id);
            procesarArticulo(ctx, articulo);
        }, RolesApp.ROLE_ADMIN, RolesApp.ROLE_AUTOR);

        app.post("/articulo/eliminar/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            eliminarArticulo(ctx, id);

        }, RolesApp.ROLE_ADMIN, RolesApp.ROLE_AUTOR);
        app.post("comentario/agregar", ctx -> {
            // Extraer datos del formulario
            String textoComentario = ctx.formParam("texto");
            long articuloId = Long.parseLong(ctx.formParam("articuloId"));

            // Validar los datos
            if (textoComentario == null || textoComentario.trim().isEmpty()) {
                ctx.status(400).result("Comentario inválido.");
                return;
            }

            // Obtener el artículo y el usuario actual
            Articulo articulo = FakeServices.getInstancia().getArticuloById(articuloId);
            Usuario usuario = ctx.sessionAttribute("usuario");

            if (articulo == null || usuario == null) {
                ctx.status(400).result("No se pudo agregar el comentario.");
                return;
            }

            // Crear el comentario y agregarlo al artículo
            Comentario nuevoComentario = new Comentario(articulo.getListaComentario().stream()
                    .mapToInt(Comentario::getId)
                    .max()
                    .orElse(0) + 1, textoComentario, usuario, articulo);

            articulo.getListaComentario().add(nuevoComentario);

            // Redirigir al artículo
            ctx.redirect("/ver/articulo/" + articulo.getId());
        });
        app.post("comentario/eliminar/{id}/{articuloId}", ctx -> {
            String id = ctx.pathParam("id");
            long articuloId = Long.parseLong(ctx.pathParam("articuloId"));
            Articulo articulo = fakeServices.getArticuloById(articuloId);
            Comentario comentario = fakeServices.getComentarioByID(Long.parseLong(id));
            articulo.getListaComentario().remove(comentario);
            eliminarComentario(ctx, id, articuloId);
            System.out.println(articuloId);


        });

        /*---- USUARIO ----*/
        app.get("/crud-usuarios/crear", (Handler) ctx -> {
            ctx.render("/template/crud/usuario/crearActualizarUsuario.html");
        }, RolesApp.ROLE_ADMIN);

        app.post("/crud-usuarios/crear/", ctx -> {
            String nombre = ctx.formParam("nombre");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String role = ctx.formParam("role");

            if (fakeServices.getUsuarioByUsername(username) != null) {
                ctx.redirect("/signup?error=Usuario ya existe");
                return;
            }
            Usuario usuario = null;
            if (role.equals("Autor")) {
                usuario = new Usuario(username, nombre, password, Set.of(RolesApp.CUALQUIERA, RolesApp.ROLE_AUTOR));
            } else if (role.equals("Usuario")) {
                usuario = new Usuario(username, nombre, password, Set.of(RolesApp.CUALQUIERA, RolesApp.USUARIO));
            }
            fakeServices.getListaUsuarios().add(usuario);
            ctx.redirect("/crud-usuario/");
        },RolesApp.ROLE_ADMIN);

        app.get("/crud-usuario/eliminar/{username}", ctx -> {
            String username = ctx.pathParam("username"); // Cambiado de ctx.formParam a ctx.pathParam
            Usuario usuario = fakeServices.getUsuarioByUsername(username);
            if (usuario != null) {
                fakeServices.eliminarUsuario(usuario);
            }
            ctx.redirect("/crud-usuario/");
        }, RolesApp.ROLE_ADMIN);

        app.get("/crud-usuario/editar/{username}", ctx -> {
            Usuario usuario = fakeServices.getUsuarioByUsername(ctx.pathParam("username"));
            Map<String,Object> model = new HashMap<>();
            model.put("usuario", usuario);
            model.put("titulo", "Crud Editar Usuario");
            model.put("action", "crud-usuario/editar" + "/" + ctx.formParam("username"));
            ctx.render("/template/crud/usuario/crearActualizarUsuario.html", model);
        },RolesApp.ROLE_ADMIN);

        app.post("/crud-usuario/editar/{username}", ctx -> {
            String username = ctx.pathParam("username");
            Usuario usuario = fakeServices.getUsuarioByUsername(username);
            if (usuario != null) {
                usuario.setUsername(ctx.formParam("username"));
                usuario.setNombre(ctx.formParam("nombre"));
                usuario.setPassword(ctx.formParam("password"));
                if (ctx.formParam("role").equals("Autor")) {
                    usuario.setListaRoles(Set.of(RolesApp.CUALQUIERA,RolesApp.ROLE_AUTOR));
                } else if (ctx.formParam("role").equals("Usuario")) {
                    usuario.setListaRoles(Set.of(RolesApp.CUALQUIERA, RolesApp.USUARIO));
                }
                fakeServices.actualizarUsuario(usuario);
                ctx.redirect("/crud-usuario/");
            } else {
                ctx.status(404).result("Usuario no encontrado");
            }
        }, RolesApp.ROLE_ADMIN);

        app.get("/crud-usuario", (Handler) ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("usuarios", fakeServices.getListaUsuarios());
            modelo.put("titulo", "CRUD USUARIOS");
            ctx.render("/template/crud/usuario/listarUsuarios.html", modelo);
        }, RolesApp.ROLE_ADMIN);


    }




    /* ---- A R T I C U L O ---- */
    public void procesarArticulo(@NotNull Context ctx, Articulo articulo) throws Exception {
        // Obtener el título y el cuerpo del artículo
        String titulo = ctx.formParam("titulo");
        String cuerpo = ctx.formParam("cuerpo");

        // Obtener etiquetas del formulario como un solo parámetro
        String etiquetasInput = ctx.formParam("etiquetasSeleccionadas");
        List<String> etiquetasSeleccionadas = new ArrayList<>();

        // Verificar si el campo de etiquetas no está vacío
        if (etiquetasInput != null && !etiquetasInput.trim().isEmpty()) {
            // Dividir la cadena de etiquetas en una lista
            etiquetasSeleccionadas = Arrays.asList(etiquetasInput.split(","));
        }

        // Crear una lista para las etiquetas
        List<Etiqueta> listaEtiquetas = new ArrayList<>();

        // Si el artículo ya existe, obtener sus etiquetas actuales
        if (articulo != null) {
            listaEtiquetas.addAll(articulo.getListaEtiqueta()); // Agregar etiquetas existentes
        }

        // Agregar solo etiquetas nuevas que no estén ya en la lista
        for (String nombreEtiqueta : etiquetasSeleccionadas) {
            String etiquetaTrimmed = nombreEtiqueta.trim();
            // Verificar si la etiqueta ya existe en la lista
            boolean existe = listaEtiquetas.stream()
                    .anyMatch(etiqueta -> etiqueta.getEtiqueta().equalsIgnoreCase(etiquetaTrimmed));

            if (!existe) {
                // Si no existe, agregar la nueva etiqueta
                listaEtiquetas.add(fakeServices.crearEtiqueta(etiquetaTrimmed));
            }
        }

        if (articulo != null) {
            // Actualizar artículo existente
            articulo.setTitulo(titulo);
            articulo.setCuerpo(cuerpo);
            articulo.setFecha(new Date());
            articulo.setListaEtiqueta(listaEtiquetas); // Establecer las etiquetas

            fakeServices.actualizarArticulo(articulo);
            ctx.redirect("/ver/articulo/" + articulo.getId());
        } else {
            // Crear un nuevo artículo
            Usuario autor = obtenerUsuarioActual(ctx);
            Articulo nuevoArticulo = new Articulo(0, titulo, cuerpo, autor, new Date(), new ArrayList<>(), listaEtiquetas);
            nuevoArticulo = fakeServices.crearArticulo(nuevoArticulo);
            ctx.redirect("/ver/articulo/" + nuevoArticulo.getId());
        }
    }


    private Usuario obtenerUsuarioActual(Context ctx) {
        final Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            throw new UnauthorizedResponse();
        }
        return usuario;
    }

    public void visualizarArticulo(@NotNull Context ctx) throws Exception {
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = fakeServices.getArticuloById(id);
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Visualizar Artículo " + articulo.getId());
        modelo.put("articulo", articulo);
        modelo.put("visualizar", true);
        modelo.put("accion", "/crud-articulo/");
        ctx.render("/templates/crud/crearEditarEliminar.html", modelo);
    }

    public void editarArticuloForm(@NotNull Context ctx) throws Exception {
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = fakeServices.getArticuloById(id);
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("titulo", "Formulario Editar Artículo " + articulo.getId());
        modelo.put("articulo", articulo);
        modelo.put("accion", "/crud-articulo/editar");
        ctx.render("/templates/crud/crearEditarEliminar.html", modelo);
    }


    public void eliminarArticulo(@NotNull Context ctx, long idArticulo) throws Exception {
        fakeServices.eliminarArticulo(idArticulo);
        ctx.redirect("/");
    }

    public void eliminarComentario(@NotNull Context ctx, String idComentario, long idArticulo) throws Exception {
        fakeServices.eliminarComentario(idComentario);
        ctx.redirect("/ver/articulo/" + idArticulo);
    }

    /* ---- USUARIO ---- */


}
