package edu.pucmm.icc352.servicios;

import edu.pucmm.icc352.modelo.Articulo;
import edu.pucmm.icc352.modelo.Etiqueta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ArticuloServices extends GestionDb<Articulo> {
    private static ArticuloServices instancia;

    private ArticuloServices(){super(Articulo.class);}

    public static ArticuloServices getInstancia(){
        if(instancia==null){instancia = new ArticuloServices();}
        return instancia;
    }

    public List<Articulo> obtenerArticulosPaginados(int pagina, int tamanioPagina) {
        EntityManager em = getEntityManager();
        TypedQuery<Articulo> query = em.createQuery("SELECT a FROM Articulo a ORDER BY a.fecha DESC", Articulo.class);
        query.setFirstResult((pagina - 1) * tamanioPagina);
        query.setMaxResults(tamanioPagina);
        List<Articulo> articulos = query.getResultList();
        em.close();
        return articulos;
    }

    public long contarTotalArticulos() {
        EntityManager em = getEntityManager();
        long total = em.createQuery("SELECT COUNT(a) FROM Articulo a", Long.class).getSingleResult();
        em.close();
        return total;
    }

    /**
     *
     * @param titulo
     * @return
     */
    public List<Articulo> findAllByTitulo(String titulo){
        EntityManager em = getEntityManager();
        Query query = em.createQuery("Select a from Articulo a where a.titulo like :titulo");
        query.setParameter("titulo", "%"+titulo+"%");
        List<Articulo> lista = query.getResultList();
        return lista;
    }

    /**
     *
     * @param listaArticulos
     * @return
     */
    public List<Articulo> getListaArticulosOrdenada(List<Articulo> listaArticulos) {
        // Ordenar los artículos alfabéticamente por su fecha
        listaArticulos.sort(Comparator.comparing(Articulo::getFecha).reversed());
        return listaArticulos;
    }

    /**
     *
     * @param etiqueta
     * @return
     */
    public List<Articulo> findArticulosByEtiqueta(Etiqueta etiqueta) {
        // Suponiendo que usas Hibernate/JPA
        EntityManager em = getEntityManager();
        Query query = em.createQuery("FROM Articulo a WHERE :etiqueta MEMBER OF a.listaEtiqueta", Articulo.class);
        query.setParameter("etiqueta", etiqueta);
        List<Articulo> lista = query.getResultList();
        return lista;
    }

    public List<Articulo> findArticulosByEtiquetas(List<Etiqueta> etiquetas) {
        if (etiquetas.isEmpty()) {
            return findAll();
        }

        EntityManager em = getEntityManager();
        TypedQuery<Articulo> query = em.createQuery(
                "SELECT DISTINCT a FROM Articulo a JOIN a.listaEtiqueta e WHERE e IN :etiquetas",
                Articulo.class
        );
        query.setParameter("etiquetas", etiquetas);

        return query.getResultList();
    }
}
