/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistibuidos;

import java.io.Serializable;

/**
 *
 * @author markerstone
 */
public class Utilizador implements Serializable{
    private String username;
    private String password;
    private String tipo;
    
    public Utilizador(String usernameParam, String passwordParam, String tipoParam){
        username = usernameParam;
        password = passwordParam;
        tipo = tipoParam;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getTipo(){
        return this.tipo;
    }

    public String getPassword(){
        return this.password;
    }
    
    public void setPassword(String passwordParam){
        password = passwordParam;
    }
    
}
