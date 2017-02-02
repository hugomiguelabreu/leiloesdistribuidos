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
    
    /*
    *
    * Construtor de leilão
    *
    */
    public Leilao(float valorInicial, String descricao, Utilizador vendedor){
        this.valor = valorInicial;
        this.descricao = descricao;
        this.vendedor = vendedor;
        this.estado = true;
        //Gerador de Id's únicos;
        this.uniqueId = UUID.randomUUID().toString();
        licitadores = new HashMap<>();
    }
    
    /*
    *
    * Obtém o valor atual do leilão;
    *
    */
    public float getValor() throws InterruptedException{
        float resultado;
        lock.readLock();
            resultado = this.valor;
        lock.readUnlock();
        return resultado;
    }
    
    /*
    *
    * Obtém a descrição do leilão;
    *
    */
    public String getDesc(){
        return this.descricao;
    }
    
    /*
    *
    * Obtém o id do leilão;
    *
    */
    public String getId(){
        return this.uniqueId;
    }
    
    /*
    *
    * Obtém o vendedor do leilão;
    *
    */
    public Utilizador getVendedor(){
        return this.vendedor.clone();
    }
    
    /*
    *
    * Obtém todos os licitadores que alguma vez licitaram no leilão;
    *
    */
    public ArrayList<Utilizador> getLicitadores() throws InterruptedException{
        ArrayList<Utilizador> res = new ArrayList<>();
        lock.readLock();
            licitadores.values().stream().forEach((u) -> {
                res.add(u);
            });
        lock.readUnlock();
        return res;
    }
    
    /*
    *
    * Obtém o estado do leilão;
    *
    */
    public boolean getEstado() throws InterruptedException{
        boolean resultado;
        lock.readLock();
            resultado =  this.estado;
        lock.readUnlock();
        return resultado;
    }
    
    /*
    *
    * Obtém o melhor licitador do leilão;
    *
    */
    public Utilizador getMelhorLicitador() throws InterruptedException{
        Utilizador resultado;
        lock.readLock();
            resultado =  this.melhorLicitador;
        lock.readUnlock();
        return resultado;
    }
    
    /*
    *
    * Faz uma licitação no leilão;
    *
    */
    public boolean licita(float valor, Utilizador user) throws InterruptedException{
        boolean resultado=false;
        lock.writeLock();
            if(valor > this.valor){
                //Verifica se é novo licitador, se for adiciona a lista de licitadores;
                if(!this.licitadores.containsKey(user.getUsername()))
                    this.licitadores.put(user.getUsername(), user);
                this.valor = valor;
                this.melhorLicitador = user;
                resultado = true;
            }
        lock.writeUnlock();
        return resultado;
    }
    
    /*
    *
    * Fecha o leilão;
    *
    */
    public void fechaLeilao() throws InterruptedException{
        lock.writeLock();
        this.estado = false;
        lock.writeUnlock();
    }
}
