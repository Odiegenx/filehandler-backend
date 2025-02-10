package org.acme.DTO;

import org.acme.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    String id;
    String cpr;
    List<String> fileLinks = new ArrayList<>();

    public UserDTO() {}
    public UserDTO(String cpr) {
        this.cpr = cpr;
    }
    public UserDTO(User user) {
        this.id = user.getId().toString();
        this.cpr = user.getCpr();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public List<String> getFileLinks() {
        return fileLinks;
    }

    public void setFileLinks(List<String> fileLinks) {
        this.fileLinks = fileLinks;
    }
}
