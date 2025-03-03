package edu.pucmm.icc352;

import edu.pucmm.icc352.controladores.*;
import edu.pucmm.icc352.servicios.UserAuthServices;
import edu.pucmm.icc352.servicios.BootStrapServices;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    //indica el modo de operacion para la base de datos.
    private static String modoConexion = "";

    public static void main(String[] args) {

        if(args.length >= 1){
            modoConexion = args[0];
            System.out.println("Modo de Operacion: "+modoConexion);
        }

        //Iniciando la base de datos.
        if(modoConexion.isEmpty()) {
            System.out.println("Modo de Operacion no valido");
            BootStrapServices.getInstancia().init();
        }

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
        app.start(7077);


        UserAuthServices.getInstance().arrancarCockroachDb();

        // Aplicar las rutas de los controladores
        new BlogControlador(app).aplicarRutas();
        new SignUpControlador(app).aplicarRutas();
        new LoginControlador(app).aplicarRutas();
        new CrudControlador(app).aplicarRutas();
    }

    public static String getModoConexion() {
        return modoConexion;
    }

}

