/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistibuidos;

/**
 *
 * @author markerstone
 */
public class Leilao {
    
    private float valor;
    private String descricao;
    private int uniqueId;
    private Utilizador vendedor;
    
    public Leilao(float valorInicial, String descricao, Utilizador vendedor){
        this.valor = valorInicial;
        this.descricao = descricao;
        this.vendedor = vendedor;
    }
    
    public float getValor(){
        return this.valor;
    }
    
    public String getDesc(){
        return this.descricao;
    }
    
    public int getId(){
        return this.uniqueId;
    }
    
}
