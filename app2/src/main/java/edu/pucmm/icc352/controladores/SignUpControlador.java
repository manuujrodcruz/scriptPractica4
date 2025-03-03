package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.servicios.UsuarioServices;
import edu.pucmm.icc352.util.RolesApp;
import io.javalin.Javalin;
import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.util.BaseControlador;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SignUpControlador extends BaseControlador {

    public SignUpControlador(Javalin app) {
        super(app);
    }

    public void aplicarRutas() {
        app.get("/signup", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Signup");
            modelo.put("error", ctx.queryParam("error")); // Mostrar error si existe
            ctx.render("/template/signup.html", modelo);
        });

        app.post("/signup", ctx -> {
            String nombre = ctx.formParam("name");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String role = ctx.formParam("role");

            // Verificar si el usuario ya existe
            if (UsuarioServices.getInstance().find(username) != null) {
                ctx.status(400); // Código de error HTTP 400 (Bad Request)
                ctx.json(Map.of("error", "El usuario ya existe"));
                return;
            }

            // Procesar la foto de perfil si se sube
            String codedPhoto = null;
            if (ctx.uploadedFile("profilePhoto") != null) {
                byte[] bytes = ctx.uploadedFile("profilePhoto").content().readAllBytes();
                codedPhoto = Base64.getEncoder().encodeToString(bytes);
            }

            // Crear usuario con el rol correspondiente
            Usuario usuario = new Usuario(username, nombre, password,
                    role.equals("Autor") ? Set.of(RolesApp.CUALQUIERA, RolesApp.ROLE_AUTOR)
                            : Set.of(RolesApp.CUALQUIERA, RolesApp.USUARIO),
                    codedPhoto);

            // Guardar el usuario en la base de datos
            UsuarioServices.getInstance().crear(usuario);

            // Guardar en sesión y devolver respuesta JSON de éxito
            ctx.sessionAttribute("usuario", usuario);
            ctx.json(Map.of("success", "Usuario registrado correctamente"));
        });

    }
}
