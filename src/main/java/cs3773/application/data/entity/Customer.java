package cs3773.application.data.entity;

import java.time.LocalDate;
import javax.persistence.Entity;

@Entity
public class Customer extends AbstractEntity {

    private String name;
    private String state;
    private String birthDate;
    private LocalDate createDate;
    private String gender;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public LocalDate getCreateDate() {
        return createDate;
    }
    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

}
