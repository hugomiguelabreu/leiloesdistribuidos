/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author markerstone
 */
public class Utilizador implements Serializable{
    private String username;
    private String password;
    private boolean tipo;
    private Queue<String> messages;
    private RwLock lock = new RwLock();
    
    public Utilizador(String usernameParam, String passwordParam, boolean tipoParam){
        username = usernameParam;
        password = passwordParam;
        tipo = tipoParam;
        messages = new LinkedList<>();
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
    
    /*
    * Verifica se o utilizador tem mensagens na caixa
    */
    public boolean hasMessages() throws InterruptedException{
        boolean res;
        lock.readLock();
            res = !(this.messages.isEmpty());
        lock.readUnlock();
        return res;
    } 
    
    /*
    * Obtém a primeira mensagem da stack
    */
    public String getMessage() throws InterruptedException{
        String s;
        lock.writeLock();
            s = this.messages.remove();
        lock.writeUnlock();
        return s;
    }
    
    /*
    * Adiciona uma mensagem a caixa do utilizador
    */
    public void addMessage(String s) throws InterruptedException{
        lock.writeLock();
            this.messages.add(s);
        lock.writeUnlock();
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
