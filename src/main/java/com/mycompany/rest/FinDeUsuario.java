/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rest;
import com.mycompany.Usuario.Usuario;
import com.mycompany.Usuario.UsuarioDAO;
import com.mycompany.Utilidades.GeneradorDeLlave;
import com.mycompany.Utilidades.UtilidadesDePasswords;
import com.mycompany.Utilidades.UtilidadesJSON;
import static com.mycompany.Utilidades.UtilidadesToken.generateJwtToken;
import com.mycompany.bdUtils.ConexionBD;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.FOUND;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
/**
 *
 * @author dark_
 */
@DeclareRoles({"admin", "user", "guest"})
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
    @PermitAll
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(@FormParam("login") String login,
                                     @FormParam("password") String password) {

        try {

            logger.log(Level.INFO, "#### nombre/password : {0}/{1}", new Object[]{login, password});

            authenticate(login, password);
            String id=UsuarioDAO.obtenerIDconNombre(login);
            String token = generateJwtToken(id);
            Usuario user = ConexionBD.obtenerUsuario(id);
            user.setToken(token);
            UsuarioDAO.setUserAuthentication(user);
            return Response.status(FOUND).header(AUTHORIZATION, "Bearer " + token).build();
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            return Response.status(UNAUTHORIZED).build();
        }
    }
    
    @POST
    @Path("/register")
    @PermitAll
    @Consumes(APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response regisrarUsuario(@FormParam("name") String login,
                                     @FormParam("password") String password){
        try {
            registro(login, password, "admin");
            
            String response = UtilidadesJSON.constructJSON("register",true);
            return Response.ok(response).build();
        } catch (Exception ex) {
            String response = UtilidadesJSON.constructJSON("register",false, "Error occured");
            return Response.ok(response).build();
        }
    }
    
    private int registro(String login, String password, String rol) throws Exception {
        int estado=0;
        password = UtilidadesDePasswords.digestPassword(password);
        boolean resultado = ConexionBD.insertarUsuario(login,password,rol);
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

    @POST
    @PermitAll
    public Response create(Usuario user) {
        em.persist(user);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(user.getId()).build()).build();
    }

    @GET
    @PermitAll
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
    @Path("/{id}/logout")
    public Response remove(@PathParam("id") String id) {
        em.remove(em.getReference(Usuario.class, id));
        return Response.noContent().build();
    }
}
