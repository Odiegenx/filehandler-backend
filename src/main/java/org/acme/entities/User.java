package org.acme.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.acme.DTO.UserDTO;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@MongoEntity(collection = "user")
public class User extends PanacheMongoEntity {
    private ObjectId id;
    private String cpr;
    //private List<String> fileLinks = new ArrayList<>();

    public User () {
    }
    public User ( String cpr) {
        this.cpr = cpr;
    }
    public User(UserDTO userDTO) {
        if(userDTO.getId() != null) {
            this.id = new ObjectId(userDTO.getId());
        }
        this.cpr = userDTO.getCpr();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

}
