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
    //  Deve ser indicada uma descrição do item a leiloar, e o sistema deve atribuir um
    //  número único a cada leilão, devolvendo esse número ao iniciador.
    private float valor;
    private String descricao;
    private int uniqueId;
    private Utilizador vendedor;
    private boolean ativo;
    
    public Leilao(float valorInicial, String descricao, Utilizador vendedor){
        this.valor = valorInicial;
        this.descricao = descricao;
        this.vendedor = vendedor;
        ativo = true;
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
    
    public boolean isAtivo(){
        return ativo;
    }
    
}
