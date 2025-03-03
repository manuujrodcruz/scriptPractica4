package edu.pucmm.icc352.servicios;

import edu.pucmm.icc352.modelo.Articulo;
import edu.pucmm.icc352.modelo.Comentario;
import edu.pucmm.icc352.modelo.Etiqueta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;

public class ComentarioServices extends GestionDb<Comentario> {

    private static ComentarioServices instance;

    private ComentarioServices() { super(Comentario.class); }

    public static ComentarioServices getItnstance() {
        if (instance == null) {
            instance = new ComentarioServices();
        }
        return instance;
    }
    /**
     *
     * @param id
     * @return
     */
    public List<Comentario> findAllById(long id){
        EntityManager em = getEntityManager();
        Query query = em.createQuery("select c from Comentario c where c.id = :id");
        query.setParameter("id", id);
        List<Comentario> lista = query.getResultList();
        return lista;
    }

    public List<Etiqueta> consultaNativa(){
        EntityManager em = getEntityManager();
        Query query = em.createNativeQuery("select * from Etiqueta ", Etiqueta.class);
        //query.setParameter("nombre", apellido+"%");
        List<Etiqueta> lista = query.getResultList();
        return lista;
    }
    public void agregarComentario(Comentario comentario) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(comentario); // Guardar el nuevo comentario en la base de datos
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback en caso de error
            }
            throw new RuntimeException("Error al agregar el comentario", e);
        } finally {
            em.close(); // Cerrar el EntityManager
        }
    }
    public void eliminarComentarioPorId(long id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin(); // Inicia la transacción

            //  Buscar el comentario por ID
            Comentario comentario = em.find(Comentario.class, id);

            if (comentario != null) {
                //  Obtener el artículo al que pertenece el comentario
                Articulo articulo = comentario.getArticulo();
                if (articulo != null) {
                    //  Eliminar el comentario de la lista del artículo
                    articulo.getListaComentario().remove(comentario);
                    em.merge(articulo); // Guardar cambios en el artículo
                }

                //  Eliminar el comentario de la base de datos
                em.remove(comentario);
            }

            tx.commit(); // Confirmar transacción
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback(); // Si hay error, revierte cambios
            }
            throw new RuntimeException("Error al eliminar el comentario", e);
        } finally {
            em.close();
        }
    }




}