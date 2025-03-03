package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.modelo.*;
import edu.pucmm.icc352.servicios.*;
import edu.pucmm.icc352.util.BaseControlador;
import edu.pucmm.icc352.util.RolesApp;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CrudControlador extends BaseControlador {

    private static final Logger log = LoggerFactory.getLogger(CrudControlador.class);
    private final UsuarioServices usuarioServices = UsuarioServices.getInstance();
    private final ArticuloServices articuloServices = ArticuloServices.getInstancia();
    private final ComentarioServices comentarioServices = ComentarioServices.getItnstance();
    private final EtiquetaServices etiquetaServices = EtiquetaServices.getInstance();

    // Constructor with Dependency Injection
    public CrudControlador(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        app.beforeMatched("/*", this::verificarAcceso);

        definirRutasArticulo();
        definirRutasComentario();
        definirRutasUsuario();
    }

    private void verificarAcceso(Context ctx) {
        // Early return to avoid unnecessary nesting
        if (ctx.routeRoles().isEmpty()) return;

        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            ctx.redirect("/login");
            return;
        }

        if (!esUsuarioAutorizado(ctx, usuario)) {
            throw new UnauthorizedResponse("No tiene roles para acceder...");
        }
    }

    private boolean esUsuarioAutorizado(Context ctx, Usuario usuario) {
        return usuarioServices.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(usuario.getUsername()))
                .flatMap(u -> u.getListaRoles().stream())
                .anyMatch(role -> ctx.routeRoles().contains(role));
    }

    private void definirRutasArticulo() {
        app.get("/articulo/crear", this::mostrarFormularioCrearArticulo, RolesApp.ROLE_AUTOR, RolesApp.ROLE_ADMIN);
        app.post("/articulo/crear/", this::procesarArticulo, RolesApp.ROLE_AUTOR, RolesApp.ROLE_ADMIN);

        app.get("/articulo/editar/{id}", this::mostrarFormularioEditarArticulo, RolesApp.ROLE_ADMIN, RolesApp.ROLE_AUTOR);
        app.post("/articulo/editar/{id}", this::procesarArticulo, RolesApp.ROLE_ADMIN, RolesApp.ROLE_AUTOR);

        app.post("/articulo/eliminar/{id}", this::eliminarArticulo, RolesApp.ROLE_ADMIN, RolesApp.ROLE_AUTOR);
        app.get("/articulos/por-etiqueta", this::obtenerArticulosPorEtiquetas);
    }

    private void mostrarFormularioCrearArticulo(Context ctx) {
        Usuario usuario = obtenerUsuarioActual(ctx);
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        model.put("titulo", "Formulario de creación de Artículo");
        model.put("accion", "/articulo/crear/");
        ctx.render("/template/crud/articulo/crearEditarEliminar.html", model);
    }

    private void mostrarFormularioEditarArticulo(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = articuloServices.find(id);
        Map<String, Object> model = new HashMap<>();
        model.put("titulo", "Formulario de Edicion de Articulo");
        model.put("action", "/articulo/editar/" + id);
        model.put("articulo", articulo);
        ctx.render("/template/crud/articulo/crearEditarEliminar.html", model);
    }

    private void eliminarArticulo(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        articuloServices.eliminar(id);
        etiquetaServices.eliminarEtiquetasHuerfanas();
        ctx.redirect("/");
    }

    private void procesarArticulo(Context ctx) {
        Articulo articulo = obtenerArticuloDeFormulario(ctx);
        String titulo = ctx.formParam("titulo");
        String cuerpo = ctx.formParam("cuerpo");
        Set<Etiqueta> listaEtiquetas = obtenerEtiquetas(ctx.formParam("etiquetasSeleccionadas"));
        for (Etiqueta etiqueta : listaEtiquetas) {
            System.out.println(etiqueta.getEtiqueta());
        }

        if (articulo == null) {
            if(!articuloServices.findAllByTitulo(titulo).isEmpty()) {
                ctx.status(400); // Código de error HTTP 400 (Bad Request)
                ctx.json(Map.of("error", "Un articulo con este titulo: " + titulo + " ya existe"));
            }else{
                crearArticulo(ctx, titulo, cuerpo, listaEtiquetas);
            }
        } else {
            if(!articuloServices.findAllByTitulo(titulo).isEmpty()) {
                ctx.status(400); // Código de error HTTP 400 (Bad Request)
                ctx.json(Map.of("error", "Un articulo con este titulo: " + titulo + " ya existe"));

            }else{
                actualizarArticulo(ctx, articulo, titulo, cuerpo, listaEtiquetas);
            }
        }
    }

    private Articulo obtenerArticuloDeFormulario(Context ctx) {
        String articuloId = null;
        try {
            articuloId = ctx.pathParam("id");
        } catch (IllegalArgumentException e) {
            // El parámetro 'id' no está presente, maneja el error aquí si es necesario
            return null;
        }

        if (articuloId == null) {
            return null;
        }

        Articulo articulo = articuloServices.find(Long.parseLong(articuloId));
        return articulo;
    }

    private void crearArticulo(Context ctx, String titulo, String cuerpo, Set<Etiqueta> listaEtiquetas) {
        Usuario autor = obtenerUsuarioActual(ctx);
        Articulo nuevoArticulo = new Articulo(titulo, cuerpo, autor, new Date(), new ArrayList<>(), new HashSet<>());

        Set<Etiqueta> managedEtiquetas = new HashSet<>();
        for (Etiqueta etiqueta : listaEtiquetas) {
            Etiqueta managedEtiqueta = etiquetaServices.findByNombre(etiqueta.getEtiqueta());

            if (managedEtiqueta == null) {
                managedEtiqueta = etiquetaServices.crear(etiqueta);
            } else {
                managedEtiqueta = etiquetaServices.merge(managedEtiqueta);
            }

            managedEtiquetas.add(managedEtiqueta);
        }

        nuevoArticulo.setListaEtiqueta(managedEtiquetas);
        articuloServices.crear(nuevoArticulo);
        ctx.redirect("/ver/articulo/" + nuevoArticulo.getId());
    }


    private void actualizarArticulo(Context ctx, Articulo articulo, String titulo, String cuerpo, Set<Etiqueta> listaEtiquetas) {
        articulo.setTitulo(titulo);
        articulo.setCuerpo(cuerpo);
        articulo.setFecha(new Date());
        articulo.setListaEtiqueta(listaEtiquetas);
        articuloServices.editar(articulo);
        ctx.redirect("/ver/articulo/" + articulo.getId());
    }

    private Set<Etiqueta> obtenerEtiquetas(String etiquetasInput) {
        Set<Etiqueta> listaEtiquetas = new HashSet<>();
        if (etiquetasInput != null && !etiquetasInput.trim().isEmpty()) {
            List<String> etiquetasSeleccionadas = Arrays.asList(etiquetasInput.split(","));
            Set<Etiqueta> todasLasEtiquetas = etiquetaServices.findAllEtiquetas();

            for (String nombreEtiqueta : etiquetasSeleccionadas) {
                String nombreNormalizado = nombreEtiqueta.trim();
                Etiqueta etiquetaExistente = etiquetaServices.findByNombre(nombreNormalizado);

                if (etiquetaExistente == null) {
                    etiquetaExistente = etiquetaServices.crear(new Etiqueta(nombreNormalizado));
                } else {
                    etiquetaExistente = etiquetaServices.merge(etiquetaExistente);
                }
                listaEtiquetas.add(etiquetaExistente);
            }
        }
        return listaEtiquetas;
    }

    private Usuario obtenerUsuarioActual(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        if (usuario == null) {
            throw new UnauthorizedResponse();
        }
        return usuario;
    }

    private void definirRutasComentario() {
        app.post("comentario/agregar", this::agregarComentario);
        app.post("comentario/eliminar/{id}/{articuloId}", this::eliminarComentario);
    }

    private void agregarComentario(Context ctx) {
        String textoComentario = ctx.formParam("texto");
        long articuloId = Long.parseLong(ctx.formParam("articuloId"));

        if (textoComentario == null || textoComentario.trim().isEmpty()) {
            ctx.status(400).result("Comentario inválido.");
            return;
        }

        // Obtener artículo asegurando que está en el contexto de persistencia
        Articulo articulo = articuloServices.find(articuloId);
        if (articulo == null) {
            ctx.status(404).result("Artículo no encontrado.");
            return;
        }

        Usuario usuario = obtenerUsuarioActual(ctx);

        // Crear el comentario sin ID
        Comentario nuevoComentario = new Comentario(textoComentario, usuario, articulo);

        // Guardar en la base de datos
        comentarioServices.crear(nuevoComentario);

        // Agregar el comentario al artículo (asegurando que el artículo está gestionado)
       // articulo.getListaComentario().add(nuevoComentario);
        articuloServices.editar(articulo); // Persistir el cambio en la lista de comentarios

        ctx.redirect("/ver/articulo/" + articulo.getId());
    }


    public void eliminarComentario(Context ctx) {
        try {
            String id = ctx.pathParam("id");
            long articuloId = Long.parseLong(ctx.pathParam("articuloId"));
            Comentario comentario = comentarioServices.find(Long.parseLong(id));


            if (comentario == null) {
                ctx.status(404).result("Comentario no encontrado");
                return;
            }

            Articulo articulo = comentario.getArticulo();
            // Actualizar la lista de comentarios del artículo antes de eliminar el comentario
            articulo.getListaComentario().remove(comentario);


            // Eliminar el comentario
            comentarioServices.eliminarComentarioPorId(Long.parseLong(id));

            ctx.redirect("/ver/articulo/" + articuloId);
        } catch (NumberFormatException e) {
            ctx.status(400).result("ID inválido");
        } catch (Exception e) {
            ctx.status(500).result("Error interno del servidor");
        }
    }


    private void definirRutasUsuario() {
        app.get("/crud-usuarios/crear", this::mostrarFormularioCrearUsuario, RolesApp.ROLE_ADMIN);
        app.post("/crud-usuarios/crear/", this::crearUsuario, RolesApp.ROLE_ADMIN);
        app.get("/crud-usuario/eliminar/{username}", this::eliminarUsuario, RolesApp.ROLE_ADMIN);
        app.get("/crud-usuario/editar/{username}", this::mostrarFormularioEditarUsuario, RolesApp.ROLE_ADMIN);
        app.post("/crud-usuario/editar/{username}", this::editarUsuario, RolesApp.ROLE_ADMIN);
        app.get("/crud-usuario", this::listarUsuarios, RolesApp.ROLE_ADMIN);
    }

    private void mostrarFormularioCrearUsuario(Context ctx) {
        Map<String,Object> model = new HashMap<>();
        model.put("accion", "/crud-usuarios/crear");
        ctx.render("/template/crud/usuario/crearActualizarUsuario.html",model);
    }

    private void crearUsuario(Context ctx) throws IOException {
        String nombre = ctx.formParam("nombre");
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String role = ctx.formParam("role");
        System.out.println(nombre + " " + username + " " + password);

        // Verificar si el usuario ya existe
        Usuario usuarioExistente = usuarioServices.findAllByUsername(username);
        if (usuarioExistente != null) {
            System.out.println("Usuario ya existe");
            ctx.redirect("/signup?error=Usuario ya existe");
            return;
        }

        // Crear el nuevo usuario
        String codedPhoto = null;
        if (ctx.uploadedFile("profilePhoto") != null) {
            byte[] bytes = ctx.uploadedFile("profilePhoto").content().readAllBytes();
            codedPhoto = Base64.getEncoder().encodeToString(bytes);
            System.out.println("Foto recibida, tamaño: " + bytes.length);
        } else {
            System.out.println("No se subió ninguna foto.");
        }

        // Crear el usuario con la foto en base64 (si existe)
        Usuario usuario = new Usuario(username, nombre, password, obtenerRoles(role), codedPhoto);
        usuarioServices.crear(usuario);
        ctx.redirect("/crud-usuario/");
    }



    private Set<RolesApp> obtenerRoles(String role) {
        if ("Autor".equals(role)) {
            return Set.of(RolesApp.CUALQUIERA, RolesApp.ROLE_AUTOR);
        } else {
            return Set.of(RolesApp.CUALQUIERA, RolesApp.USUARIO);
        }
    }

    private void eliminarUsuario(Context ctx) {
        String username = ctx.pathParam("username");
        Usuario usuario = usuarioServices.findAllByUsername(username);
        if (usuario != null) {
            usuarioServices.eliminar(usuario.getUsername());
        }
        ctx.redirect("/crud-usuario/");
    }

    private void mostrarFormularioEditarUsuario(Context ctx) {
        String username = ctx.pathParam("username");
        Usuario usuario = usuarioServices.findAllByUsername(username);
        Map<String, Object> model = new HashMap<>();
        model.put("usuario", usuario);
        model.put("titulo", "Crud Editar Usuario");
        model.put("action", "/crud-usuario/editar/" + username);
        ctx.render("/template/crud/usuario/crearActualizarUsuario.html", model);
    }

    private void editarUsuario(Context ctx) {
        String username = ctx.pathParam("username");
        Usuario usuario = usuarioServices.findAllByUsername(username);

        if (usuario != null) {
            // Actualizar los datos del usuario
            usuario.setUsername(ctx.formParam("username"));
            usuario.setNombre(ctx.formParam("nombre"));
            usuario.setPassword(ctx.formParam("password"));
            usuario.setListaRoles(obtenerRoles(ctx.formParam("role")));

            // Verificar si se sube una nueva foto de perfil
            if (ctx.uploadedFile("profilePhoto") != null) {
                try {
                    byte[] bytes = ctx.uploadedFile("profilePhoto").content().readAllBytes();
                    String codedPhoto = Base64.getEncoder().encodeToString(bytes);
                    Foto foto = FotoServices.getInstancia().findPhotoByCode(codedPhoto);
                    if (foto != null) {
                        foto = new Foto(codedPhoto);
                        FotoServices.getInstancia().editar(foto);
                        usuario.setProfilePhoto(codedPhoto); // Actualizar la foto de perfil
                    }else {
                        Foto foto2 = new Foto();
                        foto2.setFotoBase64(codedPhoto);
                        FotoServices.getInstancia().crear(foto2);
                        usuario.setProfilePhoto(codedPhoto); // Actualizar la foto de perfil
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ctx.status(500).result("Error al cargar la foto de perfil");
                    return;
                }
            }

            usuarioServices.editar(usuario);
            ctx.redirect("/crud-usuario/");
        } else {
            ctx.status(404).result("Usuario no encontrado");
        }
    }


    private void listarUsuarios(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuarios", usuarioServices.findAll());
        model.put("titulo", "CRUD USUARIOS");
        ctx.render("/template/crud/usuario/listarUsuarios.html", model);
    }

    public void obtenerArticulosPorEtiquetas(Context ctx) {
        String etiquetasParam = ctx.queryParam("etiquetas");

        List<Etiqueta> etiquetasSeleccionadas = new ArrayList<>();
        if (etiquetasParam != null && !etiquetasParam.isEmpty()) {
            List<Long> ids = Arrays.stream(etiquetasParam.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            System.out.println("Etiquetas seleccionadas: " + ids);
            etiquetasSeleccionadas = etiquetaServices.findEtiquetasByIds(ids);
        }

        for(Etiqueta etiqueta : etiquetasSeleccionadas) {
            System.out.println(etiqueta);
        }

        List<Articulo> articulos = articuloServices.findArticulosByEtiquetas(etiquetasSeleccionadas);

        ctx.attribute("articulos", articulos);
        ctx.attribute("etiquetas", etiquetaServices.findAll());
        ctx.attribute("etiquetasSeleccionadas", etiquetasSeleccionadas.stream().map(Etiqueta::getId).collect(Collectors.toList())); // Esto pasará solo los IDs
        ctx.render("/template/blog.html");
    }



}
