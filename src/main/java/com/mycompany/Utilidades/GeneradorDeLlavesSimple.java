/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.Utilidades;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 *
 * @author dark_
 */
public class GeneradorDeLlavesSimple implements GeneradorDeLlave {
    
    @Override
    public Key generarLlave() {
        String keyString = "simplekey";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
    return key;
    }
    
}
