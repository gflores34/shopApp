package cs3773.application.data.entity;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class Item extends AbstractEntity {

    private String name;
    private Integer stock;
    private String itemType;
    private Integer price;
    @Lob
    private String imgURL;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public String getImgURL() {
        return imgURL;
    }
    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

}
