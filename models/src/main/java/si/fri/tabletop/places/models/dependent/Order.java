package si.fri.tabletop.places.models.dependent;

import java.util.List;
import java.util.Date;

public class Order {
    private String id;

    private Date orderTime;

    private int table;

    private String placeId;

    private String customerId;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public int getTable() {
        return table;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public String getPlaceId(){
        return placeId;
    }

    public void setPlaceId(String id){
        this.placeId = id;
    }

    public String getCustomerId() { return customerId; }

    public void setCustomerId(String customerId) { this.customerId = customerId; }
}
