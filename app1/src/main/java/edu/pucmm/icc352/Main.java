package edu.pucmm.icc352;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Crear la instancia de Javalin
        Javalin app = Javalin.create(config -> {
            // Configuración de archivos estáticos
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico"; // Asegúrate de tener este directorio
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });
            // Configuración de plantillas Thymeleaf
            config.fileRenderer(new JavalinThymeleaf());
        }).start(7000);

        // Filtro para comprobar cookies antes de acceder a rutas
        app.before("/*", ctx -> {
            if (!ctx.path().equals("/formulario_cookie.html") && !ctx.path().equals("/check") && !ctx.path().startsWith("/css") && !ctx.path().startsWith("/jss") && !ctx.path().startsWith("/templates")) {
                if (ctx.cookie("usuario") == null || ctx.cookie("nombre") == null) {
                    ctx.redirect("/formulario_cookie.html");
                }
            }
        });

        // Ruta de verificación de formulario
        app.post("/check", ctx -> {
            // Recibiendo datos del formulario
            String usuario = ctx.formParam("usuario");
            String contrasena = ctx.formParam("contrasena");
            if (usuario == null || contrasena == null) {
                ctx.redirect("/formulario_cookie.html");
                return;
            }
            // Fake de autenticación
            ctx.cookie("usuario", usuario, 120);
            ctx.cookie("nombre", usuario, 120);
            ctx.redirect("/");
        });

        // Ruta principal
        app.get("/", ctx -> {
            String nombre = ctx.cookie("nombre");
            if (nombre == null) {
                ctx.redirect("/formulario_cookie.html"); // Redirigir si no hay sesión activa
                return;
            }
            // Crear el Map para pasar a la plantilla
            Map<String, String> model = new HashMap<>();
            model.put("nombre", nombre);

            // Renderizar la página con la plantilla y el modelo
            ctx.render("/templates/bienvenida.html", model); // Renderiza desde templates/bienvenida.html
        });
    }
}
