package si.fri.tabletop.places.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.tabletop.places.models.Place;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
public class PlacesBean {

    @Inject
    private EntityManager em;

    public List<Place> getPlaces(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Place.class, queryParameters);

    }

    public Place getPlace(String placeId) {

        Place place = em.find(Place.class, placeId);

        if (place == null) {
            throw new NotFoundException();
        }

        return place;
    }

    public Place createPlace(Place place) {

        try {
            beginTx();
            em.persist(place);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return place;
    }

    public Place putPlace(String placeId, Place place) {

        Place c = em.find(Place.class, placeId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            place.setId(c.getId());
            place = em.merge(place);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return place;
    }

    public boolean deletePlace(String placeId) {

        Place place = em.find(Place.class, placeId);

        if (place != null) {
            try {
                beginTx();
                em.remove(place);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
