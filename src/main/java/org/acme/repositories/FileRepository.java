package org.acme.repositories;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.minio.MinioAsyncClient;
import org.acme.DTO.UserDTO;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class FileRepository {

    @Inject
    MinioAsyncClient minioAsyncClient;

    String bucketName = "files";
    int expirationTimeMinutes = 60;
    int uploadExpirationTimeMinutes = 60;

    public void uploadFile(UserDTO user, String fileName, InputStream data) {
    }



    public Map<String,String> ListOfUserFileUrls(UserDTO user) throws Exception {
       try {
           Map<String,String> fileUrls = new HashMap<>();
           String prefix = user.getCpr() + "/";

           Iterable<Result<Item>> results = minioAsyncClient.listObjects(
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
            return minioAsyncClient.getPresignedObjectUrl(
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
            return minioAsyncClient.getPresignedObjectUrl(
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

    private String getUserObjectKey(String userId, String fileName) {
        return userId + "/" + fileName;
    }

}
