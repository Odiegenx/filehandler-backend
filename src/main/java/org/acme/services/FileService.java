package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;
import org.acme.repositories.FileRepository;

@ApplicationScoped
public class FileService {

    @Inject
    FileRepository fileRepository;

     public UserDTO getFileLinksByUser(UserDTO user) {
        return user;
    }
}
