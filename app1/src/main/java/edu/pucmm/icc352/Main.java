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


        // Ruta principal
        app.get("/", ctx -> {
            // Renderizar la página con la plantilla y el modelo
            ctx.render("/templates/bienvenida.html"); // Renderiza desde templates/bienvenida.html
        });
    }
}
