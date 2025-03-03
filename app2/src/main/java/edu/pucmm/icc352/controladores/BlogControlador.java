package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.modelo.Articulo;
import edu.pucmm.icc352.modelo.Etiqueta;
import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.servicios.FakeServices;
import edu.pucmm.icc352.util.BaseControlador;
import edu.pucmm.icc352.util.RolesApp;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlogControlador extends BaseControlador {
    FakeServices fakeServices = FakeServices.getInstancia();

    //constructor
    public BlogControlador(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        app.beforeMatched("/", ctx -> {
            //Si el endpoint no tiene roles asignados no es necesario verificarlo.
            if (ctx.routeRoles().isEmpty()) {
                return;
            }

            //para obtener el usuario estaré utilizando el contexto de sesion.
            final Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null) {
                throw new UnauthorizedResponse();
            }

            //listando los roles del endpoint.
            System.out.println("Los roles asignados a la ruta: " + ctx.routeRoles());

            //buscando el permiso del usuario.
            Usuario usuarioTmp = fakeServices.getListaUsuarios().stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(usuario.getUsername()))
                    .findAny()
                    .orElse(null);

            if (usuarioTmp == null) {
                System.out.println("Existe el usuario pero sin roles para acceder.");
                throw new UnauthorizedResponse("No tiene roles para acceder...");
            }

            //System.out.println("Los roles asignados en el usuario: " + usuarioTmp.getListaRoles().toString());

            //validando que el usuario registrando tiene el rol permitido.
            boolean encontrado = false;
            for (RolesApp role : usuarioTmp.getListaRoles()) {
                if (ctx.routeRoles().contains(role)) {
                    System.out.println(String.format("El Usuario: %s - con el Rol: %s tiene permiso", usuarioTmp.getUsername(), role.name()));
                    encontrado = true;
                    break;
                }
            }
            //
            if (!encontrado) {
                throw new UnauthorizedResponse("No tiene roles para acceder...");
            }
        });

        app.get("/", ctx -> {
            /* Pasar los articulos para que se coloquen en el home */
            Map<String, Object> modelo = new HashMap<>();
            List<Articulo> articulos = fakeServices.getListaArticulos();
            articulos = fakeServices.getListaArticulosOrdenada(articulos);
            Usuario usuarioActual = fakeServices.getUsuarioByUsername("Admin");
            modelo.put("articulos", articulos);
            modelo.put("etiquetas", fakeServices.getListaEtiqueta());
            modelo.put("usuario", usuarioActual);
            ctx.render("/template/blog.html", modelo);
        });

        app.get("/ver/articulo/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = fakeServices.getArticuloById(id);

            // Obtener el usuario de la sesión
            Usuario usuario = ctx.sessionAttribute("usuario"); // Esto ya es un objeto Usuario
            if (articulo != null) {
                Map<String, Object> modelo = new HashMap<>();
                modelo.put("articulo", articulo);
                modelo.put("usuario", usuario);
                ctx.render("/template/crud/articulo/visualizar.html", modelo);
            } else {
                ctx.redirect("/");
            }
        });


    }
}