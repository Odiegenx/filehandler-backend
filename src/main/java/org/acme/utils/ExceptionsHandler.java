package org.acme.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApplicationScoped
public class ExceptionsHandler {

    @Inject
    ExceptionsHandler exceptionsHandler;

    private static final Map<Class<? extends Exception>, Function<Exception, Response>> exceptionHandlers = new HashMap<>();

    static {
        exceptionHandlers.put(NullPointerException.class, e -> {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                    return Response.status(500).entity("NullPointer exception: "+e.getMessage()).type("application/json").build();
                }
        );
        exceptionHandlers.put(Exception.class, e -> {
                    System.out.println(Arrays.toString(e.getStackTrace()));
                    return Response.status(500).entity("Something bad happened: " + e.getMessage()).type("application/json").build();
                }
        );
    }

    public Response handleException(Exception e) {
        return exceptionHandlers.getOrDefault(e.getClass(),
                ex -> Response.status(500).entity("Unhandled exception: " + ex.getMessage()).type("application/json").build()
        ).apply(e);
    }
}

