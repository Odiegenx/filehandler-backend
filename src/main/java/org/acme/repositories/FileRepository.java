package org.acme.repositories;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class FileRepository {

    @Inject
    MinioAsyncClient minioClient;

    //@Inject
    //MinioClient minioClient;

    String bucketName = "files";
    int expirationTimeMinutes = 60;
    int uploadExpirationTimeMinutes = 60;


    public Map<String,String> ListOfUserFileUrls(UserDTO user) throws Exception {
       try {
           Map<String,String> fileUrls = new HashMap<>();
           String prefix = user.getCpr() + "/";

           Iterable<Result<Item>> results = minioClient.listObjects(
                   ListObjectsArgs.builder()
                           .bucket(bucketName)
                           .prefix(prefix)
                           .recursive(true)
                           .build()
           );
           for (Result<Item> result : results) {
               Item item = result.get();
               String fileName = item.objectName().substring(prefix.length());
               String fileUrl = getFileUrl(user, fileName);
               fileUrls.put(fileName, fileUrl);
           }
           return fileUrls;
       }catch (Exception e) {
           throw new Exception("Failed to list files",e);
       }
    }


    public String getFileUrl(UserDTO user , String fileName) throws Exception {
        try {
            String objectKey = getUserObjectKey(user.getCpr(), fileName);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .method(Method.GET)
                            .object(objectKey)
                            .expiry(expirationTimeMinutes, TimeUnit.MINUTES)
                            .build()
            );
        }catch (Exception e) {
            throw new Exception("Error while getting file url", e);
        }
    }

    public String getUploadUrl(UserDTO user, String fileName, String ContentType) throws Exception {
        try {
            String objectKey = getUserObjectKey(user.getCpr(), fileName);
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expirationTimeMinutes, TimeUnit.MINUTES)
                            .extraQueryParams(Map.of("Content-Type", ContentType))
                            .build()
            );
        }catch (Exception e) {
            throw new Exception("Error while getting file url", e);
        }
    }

    public InputStream getFile(UserDTO user, String fileName) throws Exception {
        try {
            String objectKey = getUserObjectKey(user.getCpr(), fileName);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build()
            ).get();
        } catch (Exception e) {
            throw new Exception("Error while getting file", e);
        }
    }


    private String getUserObjectKey(String userId, String fileName) {
        return userId + "/" + fileName;
    }

    public List<String> getUserFileNames(UserDTO userDTO) throws Exception {
        try{
            List<String> fileNames = new ArrayList<>();
            String prefix = userDTO.getCpr() + "/";
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(true)
                            .build()
            );
            for (Result<Item> result : results) {
                fileNames.add(result.get().objectName().substring(prefix.length()));
            }
            return fileNames;
        }catch (Exception e) {
            throw new Exception("Failed to list files",e);
        }
    }


    public String deleteFile(UserDTO userDTO, String fileName) throws Exception {
        try {
            String message = "";
            String objectKey = getUserObjectKey(userDTO.getCpr(), fileName);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            message = "File " + fileName + " got deleted";
            return message;
        } catch (Exception e) {
            throw new Exception("Failed to delete file",e);
        }
    }

    public String uploadFile(UserDTO userDTO, String fileName, InputStream data, String contentType) throws Exception {
        try  {
            String objectKey = getUserObjectKey(userDTO.getCpr(), fileName);
            byte[] bytes = data.readAllBytes();
            try(InputStream fileInputStream = new ByteArrayInputStream(bytes) ) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectKey)
                                .stream(fileInputStream, bytes.length, -1)
                                .contentType(contentType)
                                .build()
                ).thenApply(response -> "File " + fileName + " uploaded");
            }
            data.close();
            return "File " + fileName + " uploaded";
        }catch (Exception e) {
            throw new Exception("Failed to upload file",e);
        }
    }

    public CompletionStage<String> uploadFile1(UserDTO userDTO, String fileName, InputStream data, String contentType) throws Exception {
        try  {
            String objectKey = getUserObjectKey(userDTO.getCpr(), fileName);
            byte[] bytes = data.readAllBytes();
            InputStream fileInputStream = new ByteArrayInputStream(bytes);

            minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectKey)
                                    .stream(fileInputStream, bytes.length, -1)
                                    .contentType(contentType)
                                    .build()
                    ).thenApply(response -> "File " + fileName + " uploaded")
                    .exceptionally(e -> "File " + fileName + "failed to upload");
            fileInputStream.close();
            data.close();
        }catch (Exception e) {
            throw new Exception("Failed to upload file",e);
        };
        return null;
    }
}
