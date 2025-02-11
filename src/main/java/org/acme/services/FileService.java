package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;
import org.acme.repositories.FileRepository;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileService {

    @Inject
    FileRepository fileRepository;

     public Map<String,String> getFileLinksByUser(UserDTO user) throws Exception {

         return fileRepository.ListOfUserFileUrls(user);
    }

    public String getUploadUrlByUser(UserDTO user, String fileName, String contentType) throws Exception {
         return fileRepository.getUploadUrl(user, fileName, contentType);
    }
}
