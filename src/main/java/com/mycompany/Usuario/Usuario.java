/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.Usuario;

import com.mycompany.Utilidades.UtilidadesDePasswords;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author dark_
 */


@Table(name = "usuario")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Usuario {

    private String id;
    private String login;
    private String password;
    private String rol;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public Usuario() {
    }

    public Usuario(String id) {
        this.id = id;
    }

    public Usuario(String id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public Usuario(String login, String password, String rol, String token) {
        this.login = login;
        this.password = password;
        this.rol = rol;
        this.token = token;
    }
    
    public Usuario(String id, String login) {
        this.id = id;
        this.login = login;
    }
    
    @PrePersist
    private void setUUID() {
        id = UUID.randomUUID().toString().replace("-", "");
        password = UtilidadesDePasswords.digestPassword(password);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario user = (Usuario) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    
}
