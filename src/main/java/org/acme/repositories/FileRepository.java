package org.acme.repositories;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class FileRepository {

   /* @Inject
    MinioAsyncClient minioClient;*/

    @Inject
    MinioClient minioClient;

    String bucketName = "files";
    int expirationTimeMinutes = 60;
    int uploadExpirationTimeMinutes = 60;


    public Map<String, String> ListOfUserFileUrls(UserDTO user) throws Exception {
        try {
            Map<String, String> fileUrls = new HashMap<>();
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
        } catch (Exception e) {
            throw new Exception("Failed to list files", e);
        }
    }


    public String getFileUrl(UserDTO user, String fileName) throws Exception {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
            );
                    //.get();
        } catch (Exception e) {
            throw new Exception("Error while getting file", e);
        }
    }


    private String getUserObjectKey(String userId, String fileName) {
        return userId + "/" + fileName;
    }

    public List<String> getUserFileNames(UserDTO userDTO) throws Exception {
        try {
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
        } catch (Exception e) {
            throw new Exception("Failed to list files", e);
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
            throw new Exception("Failed to delete file", e);
        }
    }

    public String uploadFile(UserDTO userDTO, String fileName, InputStream data, String contentType,long fileSize) throws Exception {
           String objectKey = getUserObjectKey(userDTO.getCpr(), fileName);
        try {
            // Read all bytes before uploading to release the file
            byte[] bytes = data.readAllBytes();
            data.close();  // Close the original InputStream ASAP
            try (InputStream inputStream = new ByteArrayInputStream(bytes)) { // Use memory-based stream
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectKey)
                                .stream(inputStream, bytes.length, -1) // Correct part size
                                .contentType(contentType)
                                .build()
                );
                inputStream.close();
            }
        } catch (Exception e) {
            throw new Exception("Failed to upload file: " + e.getMessage(), e);
        }
        return "File " + fileName + " uploaded";
    }

    public String uploadLargeFileInParts(UserDTO user, String fileName, InputStream data, long fileSize) throws Exception {
        int PART_SIZE = 5 * 1024 * 1024; // 5MB per part
        byte[] buffer = new byte[PART_SIZE];
        int partNumber = 1;
        List<ComposeSource> sources = new ArrayList<>();
        String objectKey = getUserObjectKey(user.getCpr(), fileName);

        try (BufferedInputStream bis = new BufferedInputStream(data)) {
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                String partName = objectKey + ".part" + partNumber;

                // Upload each part
                try (ByteArrayInputStream partStream = new ByteArrayInputStream(buffer, 0, bytesRead)) {
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(partName)
                                    .stream(partStream, bytesRead, -1)
                                    .build()
                    );
                }

                // Add part to compose list
                sources.add(ComposeSource.builder()
                        .bucket(bucketName)
                        .object(partName)
                        .build());
                partNumber++;
            }
        }
        composeFile(bucketName, objectKey, sources);
        cleanUp(sources);
        return "File " + fileName + " uploaded";
    }
    public void composeFile(String bucketName, String objectName, List<ComposeSource> sources) throws Exception {
        minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .sources(sources)
                        .build()
        );
    }

    private void cleanUp(List<ComposeSource> sources) {
        // Cleanup: Delete temporary parts
        for (ComposeSource source : sources) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(source.object()) // Delete part
                                .build()
                );
            } catch (Exception e) {
                System.err.println("Failed to delete part: " + source.object());
            }
        }
    }

}

