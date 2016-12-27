/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistibuidos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author markerstone
 */
public class Leilao implements Serializable{
    
    private float valor;
    private String descricao;
    private String uniqueId;
    private Utilizador vendedor;
    private Utilizador melhorLicitador;
    private boolean estado;
    private Map<String, Utilizador> licitadores;
    
    public Leilao(float valorInicial, String descricao, Utilizador vendedor){
        this.valor = valorInicial;
        this.descricao = descricao;
        this.vendedor = vendedor;
        this.estado = true;
        this.uniqueId = UUID.randomUUID().toString();
        licitadores = new HashMap<>();
    }
    
    public float getValor(){
        return this.valor;
    }
    
    public String getDesc(){
        return this.descricao;
    }
    
    public String getId(){
        return this.uniqueId;
    }
    
    public Utilizador getVendedor(){
        return this.vendedor.clone();
    }
    
    public boolean getEstado(){
        return this.estado;
    }
    
    public Utilizador getMelhorLicitador(){
        return this.melhorLicitador;
    }
    
    public boolean licita(float valor, Utilizador user){
        synchronized(this){
            if(this.valor >= valor){
                return false;
            }else{
                if(!this.licitadores.containsKey(user.getUsername()))
                    this.licitadores.put(user.getUsername(), user);
                this.valor = valor;
                this.melhorLicitador = user;
                return true;
            }
        }
    }
    
    public void fechaLeilao(){
        this.estado = false;
        //Fazer as notificações // COMO?????
    }
}
