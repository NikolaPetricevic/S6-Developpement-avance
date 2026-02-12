package com.todolist.todolist;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/helloWorld")
public class HelloWorldController {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld(@QueryParam("name") String name) {
        return Response.ok()
                .entity("{\"message\": \"Hello World " + (name != null ? name : "") + " !\"}")
                .build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        return Response.ok()
                .entity("{\"message\": \"Hello Id " + id + " !\"}")
                .build();
    }

}
