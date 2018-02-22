/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.Usuario;

import com.mycompany.bdUtils.ConexionBD;
import static com.mycompany.bdUtils.ConexionBD.createConnection;
import com.mycompany.excepciones.ExceptionUsuarioNoEncontrado;
import static com.sun.xml.ws.security.addressing.impl.policy.Constants.logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author dark_
 */
public class UsuarioDAO {
    public static String obtenerIDconNombre(String uname) throws SQLException, Exception{
            boolean insertStatus = false;
            Connection dbConn = null;
            try{
                dbConn = createConnection();
                Statement stmt = dbConn.createStatement();
                String query = "SELECT id FROM usuario WHERE nombre = '" + uname+ "'";
                ResultSet rs = stmt.executeQuery(query);
                System.out.println(rs.toString());
                return rs.toString();
            } catch (SQLException sqle) {
			throw sqle;
            } catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if (dbConn != null) {
				dbConn.close();
			}
		}
    }
    
    
    
    public Usuario getUserAuthentication( String id ) throws ExceptionUsuarioNoEncontrado, Exception {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Usuario user = null;
		
	    try {
                Connection dbConn=ConexionBD.createConnection();
	    	stmt = dbConn.prepareStatement( "SELECT nombre, password, token, rol FROM usuario WHERE id=?;" );
	    	stmt.setString(1, id);
		    rs = stmt.executeQuery();
		    
		    if( rs.next() ) {
		    	String nombre = rs.getString("nombre");
		    	String password = rs.getString("password");
		    	String token = rs.getString("token");
		    	String rol = rs.getString("rol");
		    	
		    	user = new Usuario(nombre, password, token, rol);
		    }
		    else {
		    	throw new ExceptionUsuarioNoEncontrado( id );
		    }
		    
	    } catch ( SQLException e ) {
                System.out.println("nada");
	    }
	    finally {
	    	try {
				rs.close();
				stmt.close();
			} catch ( SQLException e ) {
                            System.out.println(e.getMessage());
			}
	    }
	    
	    return user;
    }
    
    public static boolean setUserAuthentication( Usuario user ) throws ExceptionUsuarioNoEncontrado, Exception {
		
		Connection dbConn=ConexionBD.createConnection();
	    try {
	    	Statement stmt = dbConn.createStatement();
		String query; 
                query = "UPDATE usuario SET token="+user.getToken();
                stmt.executeUpdate(query);
		System.out.println(query);
                return true;
            }catch(SQLException e){
                logger.log(Level.INFO, e.getSQLState());
            }
            return false;
    }
    
    public boolean actualizarUsuario( Usuario user ) throws ExceptionUsuarioNoEncontrado, Exception  {

		
		PreparedStatement stmt = null;
		Connection dbConn=ConexionBD.createConnection();
	    try {
	    	// prepare query
	    	StringBuffer query = new StringBuffer();
	    	query.append( "UPDATE USER SET " );
	    	
	    	boolean comma = false;
	    	List<String> prepare = new ArrayList<String>();
	    	if( user.getLogin() != null ) {
	    		if( comma ) query.append(",");
	    		query.append( "nombre=?" );
	    		prepare.add( user.getLogin() );
	    	}
	    	
	    	query.append(" WHERE id=?");
	    	stmt = dbConn.prepareStatement( query.toString() );
	    	
	    	for( int i = 0; i < prepare.size(); i++ ) {
	    		stmt.setString( i+1, prepare.get(i) );
	    	}
	    	
	    	stmt.setInt( prepare.size() + 1, Integer.parseInt( user.getId() ) );
	    	
	    	stmt.executeUpdate();
		    
	    } catch ( SQLException e ) {
	    	System.out.println(e.getMessage());
	    }
	    finally {
	    	try {
				stmt.close();
			} catch ( SQLException e ) {
				System.out.println(e.getMessage());
			}
	    }
		
		return true;
    }
    
    public List<Usuario> getAllUsers() throws Exception {

		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Usuario> user = new ArrayList<Usuario>();
		Connection dbConn=ConexionBD.createConnection();
	    try {
	    	stmt = dbConn.prepareStatement( "SELECT id, firstname, lastname, email FROM USER;" );
		    rs = stmt.executeQuery();
		    
		    while( rs.next() ) {
		    	String id = String.valueOf( rs.getInt("id") );
		    	String nombre = rs.getString("email");
		    	
		    	user.add( new Usuario(id,nombre) );
		    }
		    
	    } catch ( SQLException e ) {
	    	System.out.println(e.getMessage());
	    }
	    finally {
	    	try {
				rs.close();
				stmt.close();
			} catch ( SQLException e ) {
				System.out.println(e.getMessage());
			}
	    }
	    
	    return user;
    }
    
    public void terminarSesion(String id) throws Exception{
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Connection dbConn=ConexionBD.createConnection();
        try{
            stmt = dbConn.prepareStatement( "UPDATE usuario SET token='', rol='' WHERE id=?;" );
            stmt.setString(1, id);
            rs = stmt.executeQuery();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
}