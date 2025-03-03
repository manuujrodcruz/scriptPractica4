package edu.pucmm.icc352.util;
import io.javalin.security.RouteRole;
public enum RolesApp implements RouteRole{
    CUALQUIERA, //usuarios que no se han logueado
    USUARIO, // Los que inician sesion pero no son autores, aunque si realizan comentarios
    ROLE_AUTOR, //Autores obviamente
    ROLE_ADMIN; //El admin, con todos los roles anteriores
}
