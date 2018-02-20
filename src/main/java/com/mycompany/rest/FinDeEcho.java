/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rest;
import com.mycompany.filter.FiltroJWTToken;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
/**
 *
 * @author dark_
 */
@Path("/echo")
@Produces(TEXT_PLAIN)
public class FinDeEcho {
    
    @Inject
    private Logger logger;
    
     @GET
    public Response echo(@QueryParam("message") String message) {
        return Response.ok().entity(message == null ? "no message" : message).build();
    }

    @GET
    @Path("jwt")
    @FiltroJWTToken
    public Response echoWithJWTToken(@QueryParam("message") String message) {
        return Response.ok().entity(message == null ? "no message" : message).build();
    }
    
}
