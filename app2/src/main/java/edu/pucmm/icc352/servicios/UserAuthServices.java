package edu.pucmm.icc352.servicios;

import org.postgresql.ds.PGSimpleDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.time.Instant;

public class UserAuthServices {

    // Instancia única
    private static UserAuthServices instance;

    // Objeto para la conexión a la base de datos
    private PGSimpleDataSource dataSource;

    // Constructor privado para evitar instanciaciones externas
    private UserAuthServices() {
        //leer el JDBC
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            throw new IllegalArgumentException("Error: La variable de entorno JDBC_DATABASE_URL no está configurada o es inválida. Asegúrese de definirla antes de ejecutar la aplicación.");
        }


        dataSource = new PGSimpleDataSource();
        dataSource.setUrl(jdbcUrl);
    }

    // Método para obtener la instancia única de UserAuthService (Singleton)
    public static UserAuthServices getInstance() {
        if (instance == null) {
            synchronized (UserAuthServices.class) {
                if (instance == null) {
                    instance = new UserAuthServices();
                }
            }
        }
        return instance;
    }

    // Método para almacenar los detalles de inicio de sesión en la base de datos
    public void storeLoginDetails(String username) {
        try (Connection connection = dataSource.getConnection()) {
            // SQL para insertar la fecha y usuario
            String sql = "INSERT INTO user_logins (username, login_time) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);  // Usuario
                stmt.setTimestamp(2, Timestamp.from(Instant.now()));  // Fecha y hora actuales

                stmt.executeUpdate();  // Ejecuta la inserción
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Si hay un error, lo imprimimos
        }
    }

    // Método de prueba para arrancar la conexión
    public void arrancarCockroachDb() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Conexión exitosa a CockroachDB!");
            // Aquí puedes realizar consultas o cualquier operación sobre la base de datos
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
