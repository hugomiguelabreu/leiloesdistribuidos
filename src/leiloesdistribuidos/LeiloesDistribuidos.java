/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;

import FileManager.Read;
import FileManager.Write;
import ServerThread.BoardThread;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author markerstone
 */
public class LeiloesDistribuidos {
    
    private static Map<String, Utilizador> utilizadores;
    private static Map<String, Leilao> leiloes;
    private RwLock lock = new RwLock();
    private Map<String, BoardThread> quadroMensagens;
    
    
    public LeiloesDistribuidos() throws IOException, FileNotFoundException, ClassNotFoundException{
        Read rd = new Read();
        File fileUsers = new File("utilizadores.ld");
        File fileLeiloes = new File("leiloes.ld");
        
        //Inicia os HashMaps
        quadroMensagens = new HashMap<>();
        utilizadores = new HashMap<>();
        leiloes = new HashMap<>();

        //Verifica se existe o ficheiro que contém os utilizadores
        if(fileUsers.exists())
            utilizadores = (HashMap<String,Utilizador>)rd.readFromFile("utilizadores.ld");
        //Verifica se existe o ficheiro que contém os leiloes
        if(fileLeiloes.exists())
            leiloes = (HashMap<String,Leilao>)rd.readFromFile("leiloes.ld");
    }
    
    /*
    * Verifica se o utilizador existe
    */
    public boolean existeUtilizador(String username) throws InterruptedException{
        boolean res = false;
        lock.readLock();
        res = this.utilizadores.containsKey(username);
        lock.readUnlock();
        return res;
    }
    
    /*
    * Devolve o utilizador com o dado username --- TEM DE SER SYNCHRONIZED???
    */
    public Utilizador getUtilizador(String username) throws InterruptedException{
        Utilizador res = null;
        lock.readLock();
        if(this.utilizadores.containsKey(username))
            res = this.utilizadores.get(username).clone();
        lock.readUnlock();
        return res;
    }
    
    /*
    * Função para fazer o login de um utilizador no sistema (Aqui pode executar concorrentemente
    */
    public boolean loginUtilizador(String username, String password, Socket c) throws InterruptedException, IOException{
        Utilizador userAux;
        boolean resultado = false;
        lock.readLock();
        //Verifica se o utilizador e password estão corretos   
        if(utilizadores.containsKey(username)){
           userAux = utilizadores.get(username);
           //Verifica se a password é correta
           if(userAux.getPassword().equals(password)){
                resultado = true;
                BoardThread bt = new BoardThread(c);
                quadroMensagens.put(userAux.getUsername(), bt);
                bt.start();
            }
        }
        lock.readUnlock();
        
        return resultado;
    }
    
    public boolean logoutUtilizador(Utilizador user){
        quadroMensagens.get(user.getUsername()).interrupt();
        quadroMensagens.remove(user);
        return true;
    }
    
    /*
    * Função para registar um utilizador no sistema
    */
    public boolean registaUtilizador(String username, String password, boolean tipo) throws IOException, InterruptedException{
        boolean resultado=false;
        //Verifica se o utilizador já existe
        lock.writeLock();
        if(!utilizadores.containsKey(username)){
            //Cria o utilizador
            Utilizador user = new Utilizador(username, password, tipo);
            //Acesso único ao map para poder adicionar o utilizador
            utilizadores.put(username, user);
            resultado = true;
        }
        lock.writeUnlock();
        return resultado;
    }

    /*
    * Devolve uma lista com os leiloes
    */
    public ArrayList<Leilao> getLeiloes(){
        ArrayList<Leilao> lista = new ArrayList<>();
        for(Leilao le : this.leiloes.values())
            lista.add(le);
        return lista;
    }
    
    /*
    * Verifica se um leilao existe
    */
    public boolean existeLeilao(String id){
        return this.leiloes.containsKey(id);
    }
    
    /*
    * Devolve o leilao com o id
    */
    public Leilao getLeilao(String id){
        return this.leiloes.get(id);
    }
    
    /*
    * Função para registar um leilao no sistema
    */
    public Leilao criaLeilao(float value, String desc, Utilizador user) throws IOException{
        
        //Cria o leilao
        Leilao leilaoAux = new Leilao(value, desc, user);
        //Acesso único ao map para poder adicionar o leilao
        synchronized(leiloes){
            leiloes.put(leilaoAux.getId(), leilaoAux);
        }
        
        return leilaoAux;
    }
    
    /*
    * Função para licitar em leilão
    */
    
    public boolean licitaLeilao(Leilao leiA, float value, Utilizador user) throws InterruptedException{
        boolean resultado = false;
        resultado = leiA.licita(value, user);
        return resultado;
    }
    
    public boolean fechaLeilao(Leilao leiA, Utilizador user) throws InterruptedException{
        boolean resultado = false;
        ArrayList<Utilizador> licitadores;
        if(leiA.getVendedor().equals(user)){
            leiA.fechaLeilao();
            licitadores = leiA.getLicitadores();
            for(int i = 0; i<licitadores.size();i++){
                System.out.println(licitadores.get(i).getUsername());
                quadroMensagens.get(licitadores.get(i).getUsername()).sethasMessages("O vencedor do leilao «" + leiA.getId() + "» foi [" + 
                                    leiA.getMelhorLicitador().getUsername() + "] com licitação de *" + leiA.getValor() + "*");
                quadroMensagens.get(licitadores.get(i).getUsername()).acorda();
            }
        }
        
        return resultado;
    }
    
    /*
    * Função para presistir a "base de dados" de utilizadores num ficheiro
    */
    public boolean presisteUtilizadores() throws IOException{
        //Acesso único ao map
        synchronized(utilizadores){
            Write writer = new Write(utilizadores);
            //Escreve para um ficheiro os utilizadores
            writer.writeToFile("utilizadores.ld");
        }
        return true;
    }
    
    /*
    * Função para presistir a "base de dados" de utilizadores num ficheiro
    */
    public boolean presisteLeiloes() throws IOException{
        //Acesso único ao map
        synchronized(leiloes){
            Write writer = new Write(leiloes);
            //Escreve para um ficheiro os utilizadores
            writer.writeToFile("leiloes.ld");
        }
        return true;
    }
}
