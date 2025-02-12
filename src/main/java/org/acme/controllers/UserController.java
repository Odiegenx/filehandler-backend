package org.acme.controllers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.DTO.UserDTO;
import org.acme.entities.User;
import org.acme.services.UserService;
import org.acme.utils.ExceptionsHandler;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.util.List;

@Path("/user")
public class UserController {

    @Inject
    UserService userService;
    @Inject
    ExceptionsHandler exceptionsHandler;

    @GET
    @Path("/{cpr}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "provides a specific user", description =
            """ 
                    returns a specific user without file urls.
                    """)
    @APIResponse(responseCode = "200", description = """
            format exmaple:\n
            {
                "id": "67a9f7f4c61a8143b02f7f28",
                "cpr": "310388",
                "fileLinks": {}
            }
            """)
    public Response getUser(@PathParam("cpr") String cpr) {
        try {
            UserDTO toGet = userService.getUser(cpr);
            return Response.ok(toGet).status(Response.Status.OK).build();
        }catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "provides a list of users", description =
            """ 
                    returns a list of users without file urls.
                    """)
    @APIResponse(responseCode = "200", description = """
            format exmaple:\n
            [
                {
                    "id": "67a9f7f4c61a8143b02f7f28",
                    "cpr": "310388",
                    "fileLinks": {}
                },
                {
                    "id": "67ac799a0981e44037e3cf58",
                    "cpr": "999999",
                    "fileLinks": {}
                }
            ]
            """)
    public Response getAllUsers() {
        try{
            List<UserDTO> listOfUserDTOs = userService.getAllUsers();
            return Response.ok(listOfUserDTOs).status(Response.Status.OK).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }

    @POST
    @Path("/newUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new user", description =
            """ 
                    creates a new user, and returns the newly created user.
                    """)
    @APIResponse(responseCode = "201", description = """
            format exmaple:\n
            {
                "id": "67ac7a6b0981e44037e3cf5c",
                "cpr": "testcpr",
                "fileLinks": {}
            }
            """)
    public Response createUser(UserDTO newUser){
        try {
            UserDTO createdUser = userService.createUser(newUser);
            return Response.ok(createdUser).status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }
}
