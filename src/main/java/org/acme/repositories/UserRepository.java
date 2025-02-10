package org.acme.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entities.User;

@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {


    public User persistUser(User toPersist) throws Exception {
        try {
            persist(toPersist);
            User toReturn = toPersist;
            return toReturn;
        }catch(Exception e) {
            throw new Exception(e);
        }
    }

    public User findByCpr(String cpr) throws Exception {
        try {
            return find("cpr", cpr).firstResult();
        }catch(Exception e) {
            throw new Exception(e);
        }
    }
}
