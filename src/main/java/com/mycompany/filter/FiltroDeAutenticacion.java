/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.filter;

import com.mycompany.Usuario.Usuario;
import com.mycompany.Usuario.UsuarioDAO;
import com.mycompany.Utilidades.UtilidadesToken;
import com.mycompany.bdUtils.ConexionBD;
import com.mycompany.excepciones.ExceptionUsuarioNoEncontrado;
import com.mycompany.rest.CreadoDeRespuestas;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.jose4j.jwt.consumer.InvalidJwtException;

/**
 *
 * @author dark_
 */
@Provider
@Priority( Priorities.AUTHENTICATION )
public class FiltroDeAutenticacion implements ContainerRequestFilter {
	final static Logger logger = Logger.getLogger(FiltroDeAutenticacion.class );
	
	@Context
    private ResourceInfo resourceInfo;
	
    public static final String HEADER_PROPERTY_ID = "id";
    public static final String AUTHORIZATION_PROPERTY = "x-access-token";
    
    private static final String ACCESS_REFRESH = "Token Expirado";
    private static final String ACCESS_INVALID_TOKEN = "Token invalido";
    private static final String ACCESS_DENIED = "No estas permitido a ver este recurso";
    private static final String ACCESS_FORBIDDEN = "Acceso denegado";
    
    @Override
    public void filter( ContainerRequestContext requestContext ){
        Method method = resourceInfo.getResourceMethod();
        // everybody can access (e.g. user/create or user/authenticate)
        if( !method.isAnnotationPresent( PermitAll.class ) )
        {
            // nobody can access
            if( method.isAnnotationPresent( DenyAll.class ) ) 
            {
                requestContext.abortWith( 
                	CreadoDeRespuestas.createResponse( Response.Status.FORBIDDEN, ACCESS_FORBIDDEN )
                );
                return;
            }
              
            // get request headers to extract jwt token
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();
            final List<String> authProperty = headers.get( AUTHORIZATION_PROPERTY );
              
            // block access if no authorization information is provided
            if( authProperty == null || authProperty.isEmpty() )
            {
            	logger.warn("No token provided!");
                requestContext.abortWith( 
                    	CreadoDeRespuestas.createResponse( Response.Status.UNAUTHORIZED, ACCESS_DENIED )
                );
                return;
            }
              
            String id = null ;
            String jwt = authProperty.get(0);
            
			// try to decode the jwt - deny access if no valid token provided
			try {
				id = UtilidadesToken.validateJwtToken( jwt );
			} catch ( InvalidJwtException e ) {
				logger.warn("Invalid token provided!");
                requestContext.abortWith( 
                    	CreadoDeRespuestas.createResponse( Response.Status.UNAUTHORIZED, ACCESS_INVALID_TOKEN )
                );
                return;
			}
            
            UsuarioDAO userDao = new UsuarioDAO();
            Usuario user = null;
            try {
            	user = ConexionBD.obtenerUsuario(id);
            }
            catch ( ExceptionUsuarioNoEncontrado e ) {
            	logger.warn("Token missmatch!");
                requestContext.abortWith( 
                    	CreadoDeRespuestas.createResponse( Response.Status.UNAUTHORIZED, ACCESS_DENIED )
                );
            	return;
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(FiltroDeAutenticacion.class.getName()).log(Level.SEVERE, null, ex);
            }

        
            // token does not match with token stored in database - enforce re authentication
            if( !user.getToken().equals( jwt ) ) {
            	logger.warn("Token expired!");
                requestContext.abortWith( 
                    	CreadoDeRespuestas.createResponse( Response.Status.UNAUTHORIZED, ACCESS_REFRESH )
                );
                return;
            }
            
            // verify user access from provided roles ("admin", "user", "guest")
            if( method.isAnnotationPresent( RolesAllowed.class ) )
            {
            	// get annotated roles
                RolesAllowed rolesAnnotation = method.getAnnotation( RolesAllowed.class );
                Set<String> rolesSet = new HashSet<String>( Arrays.asList( rolesAnnotation.value() ) );
                  
                // user valid?
                if( !isUserAllowed( user.getRol(), rolesSet ) )
                {
                	logger.warn("User does not have the rights to acces this resource!");
                    requestContext.abortWith( 
                        	CreadoDeRespuestas.createResponse( Response.Status.UNAUTHORIZED, ACCESS_DENIED )
                    );
                    return;
                }
            }
            
            // set header param email for user identification in rest service - do not decode jwt twice in rest services
            List<String> idList = new ArrayList<String>();
            idList.add( id );
            headers.put( HEADER_PROPERTY_ID, idList );
        }
    }
    
    private boolean isUserAllowed( final String userRole, final Set<String> rolesSet )
    {
        boolean isAllowed = false;
          
        if( rolesSet.contains( userRole ) )
        {
            isAllowed = true;
        }
        
        return isAllowed;
    }
}