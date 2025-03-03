package edu.pucmm.icc352;

import edu.pucmm.icc352.controladores.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            // Configurando los documentos estáticos
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

            //Confifgurar el sistema de plantilla por defecto.
            config.fileRenderer(new JavalinThymeleaf());

        });

        // Iniciar el servidor en el puerto 7000
        app.start(7070);



        new BlogControlador(app).aplicarRutas();
        new SignUpControlador(app).aplicarRutas();
        new LoginControlador(app).aplicarRutas();
        new CrudControlador(app).aplicarRutas();
    }
}

