package lk.ijse.dep8.lms.dto;

import java.io.Serializable;

public class StudentDTO implements Serializable {
    private String name;
    private String email;
    private String nic;

    public StudentDTO() {
    }

    public StudentDTO(String name, String email, String nic) {
        this.name = name;
        this.email = email;
        this.nic = nic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", nic='" + nic + '\'' +
                '}';
    }
}
