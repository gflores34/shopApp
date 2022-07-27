package cs3773.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Orders extends AbstractEntity {

    private Integer custId;
    private Integer totalPrice;
    private String status;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String discountCode;

    public String getDiscountCode() {return discountCode;}
    public void setDiscountCode(String discountCode) {this.discountCode = discountCode;}
    public Integer getCustId() {
        return custId;
    }
    public void setCustId(Integer custId) {
        this.custId = custId;
    }
    public Integer getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

}
