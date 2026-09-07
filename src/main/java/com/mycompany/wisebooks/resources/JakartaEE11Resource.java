package com.mycompany.wisebooks.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("status")
public class JakartaEE11Resource {
    
    @GET
    public Response ping() {
        return Response.ok("WiseBooks API - Jakarta EE 11 running")
                .header("X-Application", "WiseBooks")
                .build();
    }
    
    @GET
    @Path("info")
    public Response info() {
        return Response.ok("{\n"
                + "  \"status\": \"online\",\n"
                + "  \"application\": \"WiseBooks\",\n"
                + "  \"version\": \"2.0.0\",\n"
                + "  \"jakarta-ee\": \"11.0\"\n"
                + "}")
                .header("Content-Type", "application/json")
                .build();
    }
}