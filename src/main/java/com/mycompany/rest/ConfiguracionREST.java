/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.rest;

import com.mycompany.filter.FiltroDeAutenticacion;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author dark_
 */
public class ConfiguracionREST extends ResourceConfig{
    ConfiguracionREST(){
        packages( "com.mycompany.filter" );
	register( FiltroDeAutenticacion.class );
    }
}
