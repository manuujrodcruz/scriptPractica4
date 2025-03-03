package edu.pucmm.icc352.servicios;

import edu.pucmm.icc352.modelo.Etiqueta;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EtiquetaServices extends GestionDb<Etiqueta> {
    private static EtiquetaServices instance;

    private EtiquetaServices() { super(Etiqueta.class); }

    public static EtiquetaServices getInstance() {
        if (instance == null) {
            instance = new EtiquetaServices();
        }
        return instance;
    }
    /**
     *
     * @param id
     * @return
     */
    public List<Etiqueta> findAllById(long id){
        try (EntityManager em = getEntityManager()) {
            // Usar createQuery para JPQL
            Query query = em.createQuery("select e from Etiqueta e where e.id = :id", Etiqueta.class);
            query.setParameter("id", id + "%"); // Comparación exacta
            List<Etiqueta> lista = query.getResultList();
            return lista;
        }
        // Cierra el EntityManager
    }

    public Etiqueta findByEtiqueta(String etiquetaTrimmed) {
        try (EntityManager em = getEntityManager()) {
            Query query = em.createQuery("select e from Etiqueta e where e.etiqueta = :etiquetaTrimmed", Etiqueta.class);
            query.setParameter("etiquetaTrimmed", etiquetaTrimmed);
            return (Etiqueta) query.getSingleResult(); // Devuelve un solo resultado
        } catch (NoResultException e) {
            return null; // Si no hay resultados, devuelve null
        }
    }

    public Set<Etiqueta> findAllEtiquetas() {
        return new HashSet<>(EtiquetaServices.getInstance().findAll());
    }
    public Etiqueta merge(Etiqueta etiqueta) {
        return this.editar(etiqueta); // Use the existing editar method
    }


    public Etiqueta findByNombre(String nombre) {
        EntityManager em = getEntityManager();
        TypedQuery<Etiqueta> query = em.createQuery("SELECT e FROM Etiqueta e WHERE e.etiqueta = :nombre", Etiqueta.class);
        query.setParameter("nombre", nombre);
        List<Etiqueta> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public void eliminarEtiquetasHuerfanas() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();  // Inicia la transacción
            em.createQuery("DELETE FROM Etiqueta e WHERE e.id NOT IN (SELECT et.id FROM Articulo a JOIN a.listaEtiqueta et)").executeUpdate();
            tx.commit(); // Si todo va bien, guarda los cambios
        } catch (Exception e) {
            tx.rollback(); // Si hay un error, revierte los cambios
            throw e;
        }
    }

    public List<Etiqueta> findEtiquetasByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        EntityManager em = getEntityManager();
        TypedQuery<Etiqueta> query = em.createQuery(
                "SELECT e FROM Etiqueta e WHERE e.id IN :ids", Etiqueta.class);
        query.setParameter("ids", ids);

        return query.getResultList();
    }



}
