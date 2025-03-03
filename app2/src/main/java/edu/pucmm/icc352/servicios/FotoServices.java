package edu.pucmm.icc352.servicios;

import edu.pucmm.icc352.modelo.Foto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.sql.PreparedStatement;
import java.util.List;

/**
 *
 */
public class FotoServices extends GestionDb<Foto> {

    private static FotoServices instancia;

    private FotoServices(){
        super(Foto.class);
    }

    public static FotoServices getInstancia(){
        if(instancia==null){
            instancia = new FotoServices();
        }
        return instancia;
    }


    public Foto findPhotoByCode(String codedPhoto) {
        if (codedPhoto == null || codedPhoto.isEmpty()) {
            return null; // Retorna null si el código está vacío
        }

        EntityManager em = getEntityManager();
        TypedQuery<Foto> query = em.createQuery(
                "SELECT f FROM Foto f WHERE f.fotoBase64 = :codigo", Foto.class // Usar el campo 'codigo' en lugar de 'id'
        );
        query.setParameter("codigo", codedPhoto); // 'codigo' es el parámetro que estás buscando

        List<Foto> result = query.getResultList();

        if (result.isEmpty()) {
            return null; // Retorna null si no encuentra la foto
        }

        return result.get(0); // Devuelve la primera foto encontrada
    }



}
