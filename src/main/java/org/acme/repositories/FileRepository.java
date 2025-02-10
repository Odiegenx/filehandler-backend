package org.acme.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.minio.MinioAsyncClient;
import org.acme.DTO.UserDTO;

import java.io.InputStream;

@ApplicationScoped
public class FileRepository {

    @Inject
    MinioAsyncClient minioAsyncClient;

    String bucketName = "files";

    public void uploadFile(UserDTO user, String fileName, InputStream data) {
    }

}
