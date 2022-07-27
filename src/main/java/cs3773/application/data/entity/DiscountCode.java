package cs3773.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class DiscountCode extends AbstractEntity {

    private Integer code;
    private Integer percentOff;
    private Integer maxDollarAmount;
    private Integer status;
    private LocalDate expirationDate;

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public Integer getPercentOff() {
        return percentOff;
    }
    public void setPercentOff(Integer percentOff) {
        this.percentOff = percentOff;
    }
    public Integer getMaxDollarAmount() {
        return maxDollarAmount;
    }
    public void setMaxDollarAmount(Integer maxDollarAmount) {
        this.maxDollarAmount = maxDollarAmount;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

}
