package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.modelo.Articulo;
import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.servicios.ArticuloServices;
import edu.pucmm.icc352.servicios.EtiquetaServices;
import edu.pucmm.icc352.servicios.UsuarioServices;
import edu.pucmm.icc352.util.BaseControlador;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogControlador extends BaseControlador {
    private final UsuarioServices usuarioServices = UsuarioServices.getInstance();
    private final ArticuloServices articuloServices = ArticuloServices.getInstancia();
    private final EtiquetaServices etiquetaServices = EtiquetaServices.getInstance();
    private static final int ARTICULOS_POR_PAGINA = 5;

    public BlogControlador(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        usuarioServices.crearAdminSiNoExiste();
        app.beforeMatched("/*", this::verificarAcceso);
        app.get("/", this::mostrarPaginaInicio);
        app.get("/ver/articulo/{id}", this::mostrarArticulo);
    }

    private void verificarAcceso(Context ctx) {
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

    private void mostrarPaginaInicio(Context ctx) {
        Usuario usuario = ctx.sessionAttribute("usuario");
        int pagina = obtenerNumeroPagina(ctx);

        List<Articulo> articulos = articuloServices.obtenerArticulosPaginados(pagina, ARTICULOS_POR_PAGINA);
        long totalArticulos = articuloServices.contarTotalArticulos();
        int totalPaginas = (int) Math.ceil((double) totalArticulos / ARTICULOS_POR_PAGINA);

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("articulos", articulos);
        modelo.put("etiquetas", etiquetaServices.findAll());
        modelo.put("usuario", usuario);
        modelo.put("paginaActual", pagina);
        modelo.put("totalPaginas", totalPaginas);

        ctx.render("/template/blog.html", modelo);
    }

    private int obtenerNumeroPagina(Context ctx) {
        String paginaParam = ctx.queryParam("pagina");
        try {
            int pagina = (paginaParam != null) ? Integer.parseInt(paginaParam) : 1;
            return Math.max(pagina, 1);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private void mostrarArticulo(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Articulo articulo = articuloServices.find(id);
        Usuario usuario = ctx.sessionAttribute("usuario");

        if (articulo != null) {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("articulo", articulo);
            modelo.put("usuario", usuario);
            ctx.render("/template/crud/articulo/visualizar.html", modelo);
        } else {
            ctx.redirect("/");
        }
    }
}