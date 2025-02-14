/*
package org.acme.repositories;

import io.minio.*;
import jakarta.inject.Inject;
import org.acme.DTO.UserDTO;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TestFileRepository {

    @Inject
    MinioAsyncClient minioAsyncClient;

    String bucketName = "files";

    public CompletableFuture<String> uploadLargeFileAsync(String uploadId, UserDTO user, String fileName, InputStream data, long fileSize) throws Exception {
        AtomicLong uploadedBytes = new AtomicLong(0);
        String objectKey = getUserObjectKey(user.getCpr(), fileName);
        List<CompletableFuture<ComposeSource>> uploadFutures = new ArrayList<>();
        int PART_SIZE = 5 * 1024 * 1024; // 5MB per part

        try (BufferedInputStream bis = new BufferedInputStream(data)) {
            byte[] buffer = new byte[PART_SIZE];
            int partNumber = 1;
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                String partName = String.format("%s.part%d", objectKey, partNumber++);
                ByteArrayInputStream partStream = new ByteArrayInputStream(buffer, 0, bytesRead);

                CompletableFuture<ComposeSource> partUpload = minioAsyncClient
                        .putObject(
                                PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(partName)
                                        .stream(partStream, bytesRead, -1)
                                        .build()
                        )
                        .thenApply(response -> {
                            // Update progress via WebSocket
                            //long progress = uploadedBytes.addAndGet(bytesRead);
                            //notifyProgress(uploadId, fileName, progress, fileSize);

                            return ComposeSource.builder()
                                    .bucket(bucketName)
                                    .object(partName)
                                    .build();
                        });

                uploadFutures.add(partUpload);
            }

            return CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0]))
                    .thenCompose(v -> {
                        List<ComposeSource> sources = uploadFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList());

                        try {
                            return composeFile(bucketName, objectKey, sources)
                                    .thenCompose(composedResult ->
                                            cleanUp(sources).thenApply(cleanupResult ->
                                                    String.format("File %s uploaded successfully", fileName)
                                            )
                                    );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

        } catch (IOException e) {
            return CompletableFuture.failedFuture(
                    new Exception("Failed to upload file: " + e.getMessage(), e)
            );
        }
    }
    private String getUserObjectKey(String userId, String fileName) {
        return userId + "/" + fileName;
    }

    private CompletableFuture<Void> composeFile(String bucket, String objectKey, List<ComposeSource> sources) throws Exception {
        return minioAsyncClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .sources(sources)
                        .build()
        ).thenApply(response -> null);
    }

    private CompletableFuture<Void> cleanUp(List<ComposeSource> sources) {
        try {
            List<CompletableFuture<Void>> cleanupFutures = new ArrayList<>();

            for (ComposeSource source : sources) {
                try {
                    CompletableFuture<Void> future = minioAsyncClient.removeObject(
                                    RemoveObjectArgs.builder()
                                            .bucket(source.bucket())
                                            .object(source.object())
                                            .build()
                            )
                            .handle((response, throwable) -> {
                               */
/* if (throwable != null) {
                                    logger.error("Error cleaning up part {}: {}",
                                            source.object(), throwable.getMessage());
                                }*//*

                                return null;
                            });
                    cleanupFutures.add(future);
                } catch (Exception e) {
                   */
/* logger.error("Error creating cleanup request for part {}: {}",
                            source.object(), e.getMessage());*//*

                    cleanupFutures.add(CompletableFuture.completedFuture(null));
                }
            }

            return CompletableFuture.allOf(cleanupFutures.toArray(new CompletableFuture[0]))
                    .exceptionally(throwable -> {
                        //logger.error("Failed to clean up all parts: {}", throwable.getMessage());
                        return null;
                    });

        } catch (Exception e) {
           // logger.error("Unexpected error during cleanup: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
}
*/
