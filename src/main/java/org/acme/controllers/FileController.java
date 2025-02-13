package org.acme.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.DTO.UserDTO;
import org.acme.forms.FileUploadForm;
import org.acme.services.FileService;
import jakarta.ws.rs.core.Response;
import org.acme.services.UserService;
import org.acme.utils.ExceptionsHandler;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;


import java.io.InputStream;
import java.util.List;
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
    @Operation(summary = "provides a user with files as urls", description =
            """ 
                    return a user with files as urls.
                    """)
    @APIResponse(responseCode = "200", description = """
            format exmaple:\n
                {
                   "id": "67a9f7f4c61a8143b02f7f28",
                   "cpr": "310388",
                   "fileLinks": {
                        "ASag.svg": "LINK",
                        "Test1.txt": "LINK",
                        "Test2.pdf": "LINK"
                   }
                }
            """)
    public Response listUserWithFilesLinks(@PathParam("userId") String userId) {
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
    @Operation(summary = "provides an url that allows for file upload", description =
            """ 
                    Make sure to set the header with Content-Type.
               
                    This url will be active for 60 min and will allow upload of a file,
                    matching the name and content type specified in the request for the url.
                    
                    The file will be added to the user's, that made the url request, folder in minio.
                    
                    REMEMBER: Using the url MUST be a put request.
                    """)
    @APIResponse(responseCode = "200", description = """
            example format:\n
            {
                "uploadUrl": "uploadUrl"
            }
            """)
    public Response getUploadFileUrl(@PathParam("userId") String userId, @PathParam("fileName") String fileName,  @HeaderParam("Content-Type") String contentType) {
        try {
            UserDTO userDTO = userService.getUser(userId);
            String uploadUrl = fileService.getUploadUrlByUser(userDTO,fileName,contentType);
            return Response.ok(Map.of("uploadUrl",uploadUrl)).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

    @GET
    @Path("/download/{userId}/{fileName}")
    @Operation(summary = "provides an inputStream of the requested file", description =
            """ 
                    provides an input stream of the requested file form the user's folder in minio.
                    """)
    @APIResponse(responseCode = "200", description = "the wanted file")
    public Response downloadFile(@PathParam("userId") String userId, @PathParam("fileName") String fileName,  @HeaderParam("Content-Type") String contentType) {
        try{
            UserDTO userDTO = userService.getUser(userId);
            return Response.ok(fileService.getFile(userDTO,fileName)).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

    @GET
    @Path("/userFiles/{userId}")
    @Operation(summary = "get a List of the names of the user's files", description =
            """ 
                    provides a list of the users files from the user's folder in minio
                    
                    can be used to request individual files from the downloadFile endpoint.
                    """)
    @APIResponse(responseCode = "200", description = "example format: [\n" +
            "    \"ASag.svg\",\n" +
            "    \"Houses.txt\",\n" +
            "    \"test2.txt\"\n" +
            "]")
    public Response getUserFileNames(@PathParam("userId") String userId) {
        try{
            UserDTO userDTO = userService.getUser(userId);
            List<String> userFileNames = fileService.getFileNames(userDTO);
            return Response.ok(userFileNames).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

    @DELETE
    @Path("/deleteFile/{userId}/{fileName}")
    @Operation(summary = "Deletes a file from the user's folder in minio", description =
            """ 
                    Simply deletes a the specific file from the user's folder in minio
                    """)
    @APIResponse(responseCode = "200", description = "File FILENAME got deleted")
    public Response deleteFile(@PathParam("userId") String userId, @PathParam("fileName") String fileName) {
        try{
            UserDTO userDTO = userService.getUser(userId);
            String message = fileService.deleteUserFile(userDTO,fileName);
            return  Response.ok(message).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }


    @POST
    @Path("/{userId}/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Uploads a file to the user's folder in minio", description =
            """
                    The endpoint expects to get formData with the file and fileName appended, example:\s
                    const formData = new FormData();
                    formData.append('file', fileInput.files[0]);
                    formData.append('fileName', fileInput.files[0].name);
                    as well as a Content-Type header matching the files type.
                    
                    If you want to test it with postman, setup the formData in the Body/form-data tap.""")

    @APIResponse(responseCode = "201", description = "File FILENAME uploaded")
    public Response uploadFile(@PathParam("userId") String userId , @BeanParam FileUploadForm form) {
        try {
            UserDTO userDTO = userService.getUser(userId);
            //byte[] file = form.getData().readAllBytes();
            String message = fileService.uploadFile(userDTO, form.getData(), form.getFileName(), form.getContentType(),form.getFileSize());
            return Response.ok(message).status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }


    @POST
    @Path("/{userId}/uploadLargeFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Uploads a file to the user's folder in minio", description =
            """
                    The endpoint expects to get formData with the file and fileName appended, example:\s
                    const formData = new FormData();
                    formData.append('file', fileInput.files[0]);
                    formData.append('fileName', fileInput.files[0].name);
                    as well as a Content-Type header matching the files type.
                    
                    If you want to test it with postman, setup the formData in the Body/form-data tap.""")

    @APIResponse(responseCode = "201", description = "File FILENAME uploaded")
    public Response uploadLargeFile(@PathParam("userId") String userId , @BeanParam FileUploadForm form) {
        try {
            UserDTO userDTO = userService.getUser(userId);
            //byte[] file = form.getData().readAllBytes();
            String message = fileService.uploadLargeFile(userDTO, form.getData(), form.getFileName(), form.getContentType(),form.getFileSize());
            return Response.ok(message).status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

}
