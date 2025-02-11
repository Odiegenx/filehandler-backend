package org.acme.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.DTO.UserDTO;
import org.acme.services.FileService;
import jakarta.ws.rs.core.Response;
import org.acme.services.UserService;
import org.acme.utils.ExceptionsHandler;

import java.util.Map;

@ApplicationScoped
@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class FileController {
    @Inject
    FileService fileService;
    @Inject
    UserService userService;
    @Inject
    ExceptionsHandler exceptionsHandler;

    @GET
    @Path("/{userId}")
    public Response listUserWithFiles(@PathParam("userId") String userId) {
        try {
            UserDTO userDTO = userService.getUser(userId);
            userDTO.setFileLinks(fileService.getFileLinksByUser(userDTO));
            return Response.ok(userDTO).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

    @GET
    @Path("/{userId}/{fileName}/upload-url")
    public Response uploadFile(@PathParam("userId") String userId, @PathParam("fileName") String fileName,  @HeaderParam("Content-Type") String contentType) {
        try {
            UserDTO userDTO = userService.getUser(userId);
            String uploadUrl = fileService.getUploadUrlByUser(userDTO,fileName,contentType);
            return Response.ok(Map.of("uploadUrl",uploadUrl)).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }
}
