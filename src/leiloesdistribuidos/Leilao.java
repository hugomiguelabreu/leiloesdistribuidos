/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;

import java.io.Serializable;
import java.util.ArrayList;
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
    private RwLock lock = new RwLock();
    
    public Leilao(float valorInicial, String descricao, Utilizador vendedor){
        this.valor = valorInicial;
        this.descricao = descricao;
        this.vendedor = vendedor;
        this.estado = true;
        this.uniqueId = UUID.randomUUID().toString();
        licitadores = new HashMap<>();
    }
    
    public float getValor() throws InterruptedException{
        float resultado;
        lock.readLock();
            resultado = this.valor;
        lock.readUnlock();
        return resultado;
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
    
    public ArrayList<Utilizador> getLicitadores() throws InterruptedException{
        ArrayList<Utilizador> res = new ArrayList<>();
        lock.readLock();
        for(Utilizador u : licitadores.values())
            res.add(u);
        lock.readUnlock();
        return res;
    }
    
    public boolean getEstado() throws InterruptedException{
        boolean resultado;
        lock.readLock();
        resultado =  this.estado;
        lock.readUnlock();
        return resultado;
    }
    
    public Utilizador getMelhorLicitador() throws InterruptedException{
        Utilizador resultado;
        lock.readLock();
            resultado =  this.melhorLicitador;
        lock.readUnlock();
        return resultado;
    }
    
    public boolean licita(float valor, Utilizador user) throws InterruptedException{
        boolean resultado=false;
        lock.writeLock();
            if(this.valor < valor){
                if(!this.licitadores.containsKey(user.getUsername()))
                    this.licitadores.put(user.getUsername(), user);
                this.valor = valor;
                this.melhorLicitador = user;
                resultado = true;
            }
        lock.writeUnlock();
        return resultado;
    }
    
    public void fechaLeilao() throws InterruptedException{
        lock.writeLock();
        this.estado = false;
        lock.writeUnlock();
    }
}
