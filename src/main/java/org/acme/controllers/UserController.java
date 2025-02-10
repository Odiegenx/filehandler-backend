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

@Path("/user")
public class UserController {

    @Inject
    UserService userService;
    @Inject
    ExceptionsHandler exceptionsHandler;

    @GET
    @Path("/{cpr}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUser(@PathParam("cpr") String cpr) {
        try {
            UserDTO toGet = userService.getUser(cpr);
            return Response.ok(toGet).status(Response.Status.OK).build();
        }catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }


    @POST
    @Path("/newUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserDTO newUser){
        try {
            UserDTO createdUser = userService.createUser(newUser);
            return Response.ok(createdUser).status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return exceptionsHandler.handleException(e);
        }
    }
}
