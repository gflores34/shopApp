package cs3773.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Sale extends AbstractEntity {

    private Integer itemId;
    private Integer percentOff;
    private LocalDate startDate;
    private LocalDate expirationDate;

    public Integer getItemId() {
        return itemId;
    }
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
    public Integer getPercentOff() {
        return percentOff;
    }
    public void setPercentOff(Integer percentOff) {
        this.percentOff = percentOff;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

}
