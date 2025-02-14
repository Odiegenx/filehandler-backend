package org.acme.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;
import org.acme.repositories.FileRepository;
import org.jboss.resteasy.reactive.server.multipart.MultipartFormDataInput;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FileService {

    @Inject
    FileRepository fileRepository;

     public Map<String,String> getFileLinksByUser(UserDTO user) throws Exception {
         // more validation logic
         return fileRepository.ListOfUserFileUrls(user);
    }

    public String getUploadUrlByUser(UserDTO user, String fileName, String contentType) throws Exception {
        // more validation logic
         return fileRepository.getUploadUrl(user, fileName, contentType);
    }

    public InputStream getFile(UserDTO userDTO, String fileName) throws Exception {
        // more validation logic
         return fileRepository.getFile(userDTO,fileName);
    }

    public List<String> getFileNames(UserDTO userDTO) throws Exception {
        // more validation logic
         return fileRepository.getUserFileNames(userDTO);
    }

    public String deleteUserFile(UserDTO userDTO, String fileName) throws Exception {
        // more validation logic
         return fileRepository.deleteFile(userDTO,fileName);
    }

    public String uploadFile(UserDTO userDTO, InputStream fileInputStream, String fileName,String contentType, long fileSize) throws Exception {
         // more validation logic
         return fileRepository.uploadFile(userDTO,fileName,fileInputStream,contentType,fileSize);
    }

    public String uploadLargeFile(UserDTO userDTO, InputStream data, String fileName, String contentType, long fileSize) throws Exception {
         return fileRepository.uploadLargeFileInParts(userDTO,fileName,data,fileSize);
    }

    public String getFileUrl(UserDTO userDTO, String fileName) throws Exception {
         //  more validation logic
        return fileRepository.getFileUrl(userDTO,fileName);
    }
}
