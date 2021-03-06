/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.bdUtils;
import com.mycompany.Usuario.Usuario;
import com.mycompany.excepciones.ExceptionUsuarioNoEncontrado;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
/**
 *
 * @author dark_
 */
public class ConexionBD {
    @SuppressWarnings("finally")
	public static Connection createConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(ConstantesBD.dbClass);
			con = DriverManager.getConnection(ConstantesBD.dbUrl, ConstantesBD.dbUser, ConstantesBD.dbPwd);
		} catch (Exception e) {
			throw e;
		} finally {
			return con;
		}
	}
        
	public static boolean checkLogin(String uname, String pwd) throws Exception {
		boolean isUserAvailable = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = ConexionBD.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Statement stmt = dbConn.createStatement();
			String query = "SELECT * FROM usuario WHERE nombre = '" + uname
					+ "' AND password= '" + pwd + "'";
			ResultSet rs = stmt.executeQuery(query);
                        System.out.println(rs);
			while (rs.next()) {
				isUserAvailable = true;
			}
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
		return isUserAvailable;
	}
        
        public static String TodosLosUsuarios()throws SQLException, Exception{
            Connection dbConn = null;
            return "";
        }
        
	public static boolean insertarUsuario(String name, String pwd, String rol) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			try {
				dbConn = ConexionBD.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
                        String id = UUID.randomUUID().toString().replace("-", "");
			Statement stmt = dbConn.createStatement();
			String query = "INSERT into usuario(id, nombre, password, rol) values('"+id+"','"+name+ "',"+ "'" + pwd + "','"+rol+"')";
			System.out.println(query);
                        int records = stmt.executeUpdate(query);
                        
			if (records > 0) {
				insertStatus = true;
			}
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
		return insertStatus;
	}
        
        public static Usuario obtenerUsuario (String id) throws ExceptionUsuarioNoEncontrado, Exception{
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario user = null;
        try{
            Connection dbConn=ConexionBD.createConnection();
            
            stmt=dbConn.prepareStatement("SELECT id, nombre FROM usuario WHERE id=;");
            stmt.setString(1,id);
            rs=stmt.executeQuery();
            if(rs.next()){
                String uid = rs.getString("id");
		String nombre = rs.getString("nombre");
                user = new Usuario(uid,nombre);
                return user;
            }else{
                ExceptionUsuarioNoEncontrado ex = new ExceptionUsuarioNoEncontrado(id);
            }     
        }catch(SQLException e){

        } catch (Exception ex) {
        }
        finally{
            try{
                rs.close();
                stmt.close();
            }catch ( SQLException e ) {

            }
        }
        return user;
    }
}
