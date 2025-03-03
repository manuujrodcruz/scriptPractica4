package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.servicios.FakeServices;
import edu.pucmm.icc352.util.BaseControlador;
import io.javalin.Javalin;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class LoginControlador extends BaseControlador {

    private final FakeServices fakeServices = FakeServices.getInstancia();

    public LoginControlador(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        // Ruta para mostrar el formulario de login
        app.get("/login", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Login");
            ctx.render("/template/login.html", modelo);

        });

        // Ruta para procesar el login
        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            Usuario usuario = fakeServices.getUsuarioByUsername(username);
            if (usuario != null && usuario.getPassword().equals(password)) {
                // Almacenar el usuario en la sesión
                ctx.sessionAttribute("usuario", usuario);
                ctx.sessionAttribute("mensaje", "Inicio de sesión exitoso");
                ctx.status(200); // Código de éxito
            } else {
                // Si las credenciales son incorrectas
                ctx.status(401); // Código de error
            }
        });

        // Ruta para cerrar sesión
        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate(); // Cierra la sesión actual
            ctx.redirect("/"); // Redirige a la página de login
        });

        app.get("/switch-account", ctx -> {
            ctx.req().getSession().invalidate(); // Cierra sesión y pide nueva autenticación
            ctx.redirect("/login");
        });
    }
}
