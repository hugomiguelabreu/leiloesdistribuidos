/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author markerstone
 */
public class Utilizador implements Serializable{
    private String username;
    private String password;
    private boolean tipo;
    
    public Utilizador(String usernameParam, String passwordParam, boolean tipoParam){
        username = usernameParam;
        password = passwordParam;
        tipo = tipoParam;
    }
    
    public Utilizador(Utilizador u){
        username = u.getUsername();
        password = u.getPassword();
        tipo = u.getTipo();
    }
    
    public String getUsername(){
        return this.username;
    }
    /*
    * True para comprador
    * False Vendedor
    */
    public boolean getTipo(){
        return this.tipo;
    }

    public String getPassword(){
        return this.password;
    }
    
    public void setPassword(String passwordParam){
        password = passwordParam;
    }
    
    public Utilizador clone(){
        return new Utilizador(this);
    }
    
    public boolean equals(Utilizador user){
        if(user.getUsername().equals(this.getUsername()) &&
                user.getPassword().equals(this.getPassword()) &&
                    user.getTipo() == this.getTipo())
            return true;
        else
            return false;
    }
    
}
