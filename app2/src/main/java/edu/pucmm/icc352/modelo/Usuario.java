package edu.pucmm.icc352.modelo;

import edu.pucmm.icc352.util.RolesApp;

import java.util.Set;

public class Usuario {
    private String username;
    private String nombre;
    private String password;
    //private boolean administrator;
    //private boolean autor;
    Set<RolesApp> listaRoles;

    public Usuario(String username, String nombre, String password, Set<RolesApp> listaRoles) {
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.listaRoles = listaRoles;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RolesApp> getListaRoles() {
        return listaRoles;
    }

    public void setListaRoles(Set<RolesApp> listaRoles) {
        this.listaRoles = listaRoles;
    }

    public boolean tieneRol(RolesApp rol) {

        return listaRoles.contains(rol);

    }


    public boolean tieneRol(String rol) {

        return listaRoles.stream()

                .anyMatch(r -> r.name().equals(rol));

    }

    public String getRolesSplitted(Usuario usuario) {
        // Comprobamos si la lista de roles del usuario es nula o vacía
        if (usuario != null && usuario.getListaRoles() != null) {
            // Creamos un StringBuilder para concatenar los roles
            StringBuilder rolesStr = new StringBuilder();

            // Recorremos la lista de roles y los añadimos al StringBuilder
            for (RolesApp rol : usuario.getListaRoles()) {
                if (rolesStr.length() > 0) {
                    rolesStr.append(", "); // Añadimos una coma y un espacio entre los roles
                }
                rolesStr.append(rol.name()); // Añadimos el nombre del rol
            }

            return rolesStr.toString(); // Devolvemos la cadena concatenada
        }

        return ""; // Si no hay roles, devolvemos una cadena vacía
    }

}
