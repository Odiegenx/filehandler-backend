package org.acme.DTO;

import com.google.common.collect.Maps;
import org.acme.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDTO {
    String id;
    String cpr;
    Map<String, String> fileLinks = new HashMap<String, String>();

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

    public Map<String, String> getFileLinks() {
        return fileLinks;
    }

    public void setFileLinks(Map<String, String> fileLinks) {
        this.fileLinks = fileLinks;
    }
}
