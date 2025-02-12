package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;
import org.acme.entities.User;
import org.acme.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public UserDTO createUser(UserDTO newUser) throws Exception {
        User toPersist = new User(newUser);
        return new UserDTO(userRepository.persistUser(toPersist));
    }

    public UserDTO getUser(String cpr) throws Exception {
        User toFind = new User(cpr);
        return new UserDTO(userRepository.findByCpr(cpr));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAllUser().stream().map(UserDTO::new).toList();
    }
}
