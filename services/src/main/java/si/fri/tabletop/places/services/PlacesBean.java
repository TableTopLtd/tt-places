package si.fri.tabletop.places.services;

import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import si.fri.tabletop.places.models.dependent.Menu;
import si.fri.tabletop.places.models.dependent.Order;
import si.fri.tabletop.places.models.Place;
import si.fri.tabletop.places.services.config.RestProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PlacesBean {

    private Logger log = LogManager.getLogger(PlacesBean.class.getName());

    @Inject
    private RestProperties restProperties;

    @Inject
    private EntityManager em;

    @Inject
    private PlacesBean placesBean;

    private Client httpClient;

    // TODO: Change when we have config server
    @Inject
    @DiscoverService("tt-menus")
    private Optional<String> baseUrl;

    @Inject
    @DiscoverService("tt-orders")
    private Optional<String> ordersUrl;


    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

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

        // TODO: Change when we have config server
        //if (restProperties.isMenuServiceEnabled()) {
        List<Menu> menus = placesBean.getMenus(placeId);
        place.setMenus(menus);

        List<Order> orders = placesBean.getActiveOrders(placeId);
        place.setOrders(orders);
        //}

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


    public List<Menu> getMenus(String placeId) {

        if (baseUrl.isPresent()) {
            log.info("BASE URL REQUEST TO "+baseUrl.get());
            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/menus?where=placeId:EQ:" + placeId)
                        .request().get(new GenericType<List<Menu>>() {
                        });
            }catch (WebApplicationException | ProcessingException e) {
                log.error(e);
                return new ArrayList<>();
                //throw new InternalServerErrorException();
            }
        }
        return new ArrayList<>();
    }

    @CircuitBreaker
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getActiveOrdersFallback")
    public List<Order> getActiveOrders(String placeId){
        try {
            return httpClient
                    .target(ordersUrl.get() + "/v1/orders?where=placeId:EQ:" + placeId)
                    .request().get(new GenericType<List<Order>>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.error(e);
            return new ArrayList<>();
        }
    }

    public List<Order> getActiveOrdersFallback(String placeId) {
        System.out.print("Inside fallback method");
        List<Order> orders = new ArrayList<>();

        Order order = new Order();

        order.setTable(-1);
        order.setOrderTime(new Date());
        order.setId("-1");

        orders.add(order);

        return orders;
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
