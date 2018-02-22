/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rest;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
/**
 *
 * @author dark_
 */
@DeclareRoles({"admin", "user", "guest"})


@Path("/echo")
@Produces(TEXT_PLAIN)
public class FinDeEcho {
    
    @Inject
    private Logger logger;
    
     @GET
     @PermitAll
    public Response echo(@QueryParam("message") String message) {
        return Response.ok().entity(message == null ? "no message" : message).build();
    }

    @GET
    @RolesAllowed({"admin","user"})
    @Path("jwt")
    public Response echoWithJWTToken(@QueryParam("message") String message) {
        return Response.ok().entity(message == null ? "no message" : message).build();
    }
    
}
