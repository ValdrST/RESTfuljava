/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rest;
import com.mycompany.Usuario.Usuario;
import com.mycompany.Utilidades.GeneradorDeLlave;
import com.mycompany.Utilidades.UtilidadesDePasswords;
import com.mycompany.Utilidades.UtilidadesJSON;
import com.mycompany.bdUtils.ConexionBD;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
/**
 *
 * @author dark_
 */
@Path("/users")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Transactional
public class FinDeUsuario {
    @Context
    private UriInfo uriInfo;

    @Inject
    private Logger logger;

    @Inject
    private GeneradorDeLlave generadorDeLlave;

    @PersistenceContext
    private EntityManager em;
    
    @POST
    @Path("/login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@FormParam("login") String login,
                                     @FormParam("password") String password) {

        try {

            logger.info("#### login/password : " + login + "/" + password);

            // Authenticate the user using the credentials provided
            authenticate(login, password);

            // Issue a token for the user
            String token = issueToken(login);

            // Return the token on the response
            return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();

        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
    }
    @POST
    @Path("/register")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String regisrarUsuario(@FormParam("name") String login,
                                     @FormParam("password") String password){
        try {
            registro(login, password);
            String response = UtilidadesJSON.constructJSON("register",true);
            return response;
        } catch (Exception ex) {
            return UtilidadesJSON.constructJSON("register",false, "Error occured");
        }
    }
    
    private int registro(String login, String password) throws Exception {
        int estado=0;
        password = UtilidadesDePasswords.digestPassword(password);
        boolean resultado = ConexionBD.insertarUsuario(login,password);
        if(resultado==true){
            estado = 1;
        }else 
            throw new SecurityException("Error en registro");
        return estado;
    }

    private void authenticate(String login, String password) throws Exception {
        boolean resultado;
        password = UtilidadesDePasswords.digestPassword(password);
        resultado = ConexionBD.checkLogin(login, password);
        if ( resultado == false)
            throw new SecurityException("Usuario o contraseña invalidos");
    }

    private String issueToken(String login) {
        Key key = generadorDeLlave.generarLlave();
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        logger.log(Level.INFO, "#### generar token de usuario: {0} - {1}", new Object[]{jwtToken, key});
        return jwtToken;
    }

    @POST
    public Response create(Usuario user) {
        em.persist(user);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(user.getId()).build()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        Usuario user = em.find(Usuario.class, id);

        if (user == null)
            return Response.status(NOT_FOUND).build();

        return Response.ok(user).build();
    }

    @GET
    public Response findAllUsers() {
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response remove(@PathParam("id") String id) {
        em.remove(em.getReference(Usuario.class, id));
        return Response.noContent().build();
    }

    // ======================================
    // =          Private methods           =
    // ======================================

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
