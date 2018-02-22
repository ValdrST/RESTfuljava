/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.excepciones;

/**
 *
 * @author dark_
 */
public class ExceptionUsuarioNoEncontrado extends RuntimeException{
    public ExceptionUsuarioNoEncontrado( String id) {
		super("User not found: " + id );
    }
}
