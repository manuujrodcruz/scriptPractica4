package edu.pucmm.icc352.controladores;

import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.servicios.UsuarioServices;
import edu.pucmm.icc352.util.BaseControlador;
import io.javalin.Javalin;
import jakarta.servlet.http.Cookie;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginControlador extends BaseControlador {

    private static final String SECRET_KEY = "ClaveSegura123!";
    private static final StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();

    public LoginControlador(Javalin app) {
        super(app);
    }

    // Método para encriptar texto (Ej: Nombre de usuario en cookies)
    private static String encrypt(String plainText) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(SECRET_KEY);
        return textEncryptor.encrypt(plainText);
    }

    @Override
    public void aplicarRutas() {

        app.get("/login", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Login");
            ctx.render("/template/login.html", modelo);
        });

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");
            boolean rememberMe = "on".equals(ctx.formParam("rememberMe"));

            //  Debug: Imprimir credenciales ingresadas
            System.out.println(" Username ingresado: " + username);
            System.out.println(" Password ingresada: " + password);

            Usuario usuario = UsuarioServices.getInstance().find(username);
            if (usuario != null) {
                System.out.println(" Usuario encontrado: " + usuario.getUsername());
                System.out.println(" Password almacenado: " + usuario.getPassword());
            } else {
                System.out.println(" Usuario no encontrado en la base de datos.");
            }

            // Comprobación de contraseña encriptada
            if (usuario != null && usuario.getPassword().equals(password)) {
                ctx.sessionAttribute("usuario", usuario);
                System.out.println(" Usuario autenticado correctamente.");

                // Si el usuario seleccionó "Recordarme"
                if (rememberMe) {
                    String encryptedUsername = encrypt(username);
                    String encodedUsername = URLEncoder.encode(encryptedUsername, "UTF-8");

                    Cookie cookie = new Cookie("rememberMe", encodedUsername);
                    cookie.setMaxAge(7 * 24 * 60 * 60); // 1 semana
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true); // En producción debe ser true
                    cookie.setPath("/");
                    cookie.setAttribute("SameSite", "Lax");

                    ctx.res().addCookie(cookie);
                    System.out.println(" Cookie 'rememberMe' creada correctamente.");
                } else {
                    System.out.println(" Usuario no seleccionó 'Recordarme'.");
                }

                ctx.json(Map.of("mensaje", "Inicio de sesión exitoso"));
            } else {
                System.out.println(" Credenciales incorrectas.");
                ctx.status(401);
                ctx.json(Map.of("error", "Credenciales incorrectas"));
            }
        });
        //Cambiar cuenta
        app.get("/switch-account", ctx -> {
            ctx.req().getSession().invalidate(); // Cierra sesión y pide nueva autenticación
            // Eliminar cookie
            Cookie cookie = new Cookie("rememberMe", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            ctx.res().addCookie(cookie);
            ctx.redirect("/login");
        });
        // Cerrar sesión
        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();

            // Eliminar cookie
            Cookie cookie = new Cookie("rememberMe", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            ctx.res().addCookie(cookie);

            System.out.println(" Usuario deslogueado y cookie eliminada.");
            ctx.redirect("/");
        });
    }
}
