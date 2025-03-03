package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.servicios.FakeServices;
import edu.pucmm.icc352.util.RolesApp;
import io.javalin.Javalin;
import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.util.BaseControlador;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SignUpControlador extends BaseControlador {

    private final FakeServices fakeServices = FakeServices.getInstancia();

    public SignUpControlador(Javalin app) {
        super(app);
    }


    public void aplicarRutas() {
        app.get("/signup", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Signup");
            ctx.render("/template/signup.html", modelo);

        });

        // Ruta para procesar el login
        app.post("/signup", ctx -> {
            String nombre = ctx.formParam("name");
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            String role = ctx.formParam("role");

            if (fakeServices.getUsuarioByUsername(username) != null) {
                ctx.redirect("/signup?error=Usuario ya existe");
                return;
            }
            Usuario usuario = null;
            if(role.equals("Autor")) {
                 usuario = new Usuario(username, nombre, password, Set.of(RolesApp.CUALQUIERA, RolesApp.ROLE_AUTOR));
            }else if (role.equals("Usuario")) {
                 usuario = new Usuario(username,nombre,password, Set.of( RolesApp.CUALQUIERA, RolesApp.USUARIO));
            }
            fakeServices.getListaUsuarios().add(usuario);
            ctx.sessionAttribute("usuario", usuario);
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("usuario", usuario);
            ctx.redirect("/");

        });
    }
}
