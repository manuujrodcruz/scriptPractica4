package edu.pucmm.icc352.servicios;

import edu.pucmm.icc352.modelo.Usuario;
import edu.pucmm.icc352.util.RolesApp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Set;

public class UsuarioServices extends GestionDb<Usuario> {
    private static UsuarioServices instance;

    private UsuarioServices() { super(Usuario.class); }

    public static UsuarioServices getInstance() {
        if (instance == null) {
            instance = new UsuarioServices();
        }
        return instance;
    }
    /**
     *
     * @param username
     * @return
     */
    public Usuario findAllByUsername(String username) {
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select u from Usuario u where u.username = :username");
        query.setParameter("username", username);
        List<Usuario> lista = query.getResultList();

        if (lista.isEmpty()) {
            return null; // No se encontró ningún usuario
        }

        return lista.get(0); // Devuelve el primer usuario encontrado
    }


    public Usuario crearAdminSiNoExiste() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();

        // Verificar si el usuario admin ya existe
        Query query = em.createQuery("SELECT u FROM Usuario u WHERE u.username = :username");
        query.setParameter("username", "admin");
        List<Usuario> resultado = query.getResultList();

        Usuario admin;
        if (resultado.isEmpty()) {
            // Si no existe, lo creamos
            admin = new Usuario();
            admin.setUsername("admin");
            admin.setNombre("admin");
            admin.setPassword("admin"); // ⚠️ Debes encriptar la contraseña en un entorno real
            admin.setListaRoles(Set.of(RolesApp.CUALQUIERA,RolesApp.USUARIO,RolesApp.ROLE_AUTOR,RolesApp.ROLE_ADMIN));

            em.persist(admin);
            System.out.println("Usuario admin creado.");
        } else {
            admin = resultado.get(0);
            System.out.println("El usuario admin ya existe.");
        }

        em.getTransaction().commit();
        return admin;
    }

}
