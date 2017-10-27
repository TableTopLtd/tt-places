package si.fri.tabletop.places;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "places")
@NamedQueries(value =
        {
                @NamedQuery(name = "Place.getAll", query = "SELECT p FROM places p"),
        })
@UuidGenerator(name = "idGenerator")
public class Place {

    // TODO replace attributes with real ones
    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String title;

    private String description;

    @Column()
    private String placeType;

    private String address;

    @Column(name = "postal_code")
    private int postalCode;

    @Column(name = "country_code")
    private String countryCode;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return placeType;
    }

    public void setType(String type) {
        this.placeType = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
