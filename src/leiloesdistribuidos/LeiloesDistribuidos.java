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
    
    private Map<String, Utilizador> utilizadores;
    private Map<String, Leilao> leiloes;
    private RwLock lockUtilizadores = new RwLock();
    private RwLock lockLeiloes = new RwLock();
    private RwLock lockQuadro = new RwLock();
    //Cada utilizador tem uma Thread que trata das mensagens;
    private Map<String, BoardThread> quadroMensagens;
    
    /*
    *
    * Construtor da classe
    *
    */
    public LeiloesDistribuidos() throws IOException, FileNotFoundException, ClassNotFoundException{
        Read rd = new Read();
        File fileUsers = new File("utilizadores.ld");
        File fileLeiloes = new File("leiloes.ld");
        
        //Inicia os HashMaps
        quadroMensagens = new HashMap<>();
        utilizadores = new HashMap<>();
        leiloes = new HashMap<>();

        //Verifica se existe o ficheiro que contém os utilizadores, se existir carrega;
        if(fileUsers.exists())
            utilizadores = (HashMap<String,Utilizador>)rd.readFromFile("utilizadores.ld");
        //Verifica se existe o ficheiro que contém os leiloes, se existir carrega;
        if(fileLeiloes.exists())
            leiloes = (HashMap<String,Leilao>)rd.readFromFile("leiloes.ld");
    }
    
    /*
    * Verifica se o utilizador existe
    */
    public boolean existeUtilizador(String username) throws InterruptedException{
        boolean res = false;
        lockUtilizadores.readLock();
            res = this.utilizadores.containsKey(username);
        lockUtilizadores.readUnlock();
        return res;
    }
    
    /*
    * Devolve o utilizador com o dado username
    */
    public Utilizador getUtilizador(String username) throws InterruptedException{
        Utilizador res = null;
        lockUtilizadores.readLock();
            if(this.utilizadores.containsKey(username))
                res = this.utilizadores.get(username);
        lockUtilizadores.readUnlock();
        return res;
    }
    
    /*
    * Função para fazer o login de um utilizador no sistema;
    */
    public boolean loginUtilizador(String username, String password, Socket c) throws InterruptedException, IOException{
        Utilizador userAux;
        boolean resultado = false;
        lockUtilizadores.readLock();
            //Verifica se o utilizador e password estão corretos && se não existe o mesmo utilizador já logado 
            if(utilizadores.containsKey(username) && !quadroMensagens.containsKey(username)){
               userAux = utilizadores.get(username);
               //Verifica se a password é correta
               if(userAux.getPassword().equals(password)){
                    resultado = true;
                    //Cria a Thread de mensagens para o utilizador que acabou de fazer login;
                    BoardThread bt = new BoardThread(c, userAux);
                    lockQuadro.writeLock();
                        quadroMensagens.put(userAux.getUsername(), bt); 
                    lockQuadro.writeUnlock();
                    //Inicia a Thread das mensagens
                    bt.start();
                }
            }
        lockUtilizadores.readUnlock();
        
        return resultado;
    }
    
    /*
    * Função para fazer o logout de um utilizador do sistema;
    */
    public boolean logoutUtilizador(Utilizador user) throws InterruptedException{
        //Caso o utilizador faça logout pára a Thread de mensagens dele;
        lockQuadro.readLock();
            quadroMensagens.get(user.getUsername()).interrupt();
        lockQuadro.readUnlock();
        lockQuadro.writeLock();
            quadroMensagens.remove(user.getUsername());
        lockQuadro.writeUnlock();
        return true;
    }
    
    /*
    * Função para registar um utilizador no sistema
    */
    public boolean registaUtilizador(String username, String password, boolean tipo) throws IOException, InterruptedException{
        boolean resultado=false;
        //Verifica se o utilizador já existe
        lockUtilizadores.writeLock();
            if(!utilizadores.containsKey(username)){
                //Cria o utilizador
                Utilizador user = new Utilizador(username, password, tipo);
                utilizadores.put(username, user);
                resultado = true;
            }
        lockUtilizadores.writeUnlock();
        return resultado;
    }

    /*
    * Devolve uma lista com os leiloes
    */
    public ArrayList<Leilao> getLeiloes() throws InterruptedException{
        ArrayList<Leilao> lista = new ArrayList<>();
        lockLeiloes.readLock();
            this.leiloes.values().stream().forEach((le) -> {
                lista.add(le);
            });
        lockLeiloes.readUnlock();
        return lista;
    }
    
    /*
    * Verifica se um leilao existe
    */
    public boolean existeLeilao(String id) throws InterruptedException{
        boolean resultado;
        lockLeiloes.readLock();
            resultado = this.leiloes.containsKey(id);
        lockLeiloes.readUnlock();
        return resultado;
    }
    
    /*
    * Devolve o leilao com o id
    */
    public Leilao getLeilao(String id) throws InterruptedException{
        Leilao res;
        lockLeiloes.readLock();
            res = this.leiloes.get(id);
        lockLeiloes.readUnlock();
        return res;
    }
    
    /*
    * Função para registar um leilao no sistema
    */
    public Leilao criaLeilao(float value, String desc, Utilizador user) throws IOException, InterruptedException{
        
        //Cria o leilao
        Leilao leilaoAux = new Leilao(value, desc, user);
        //Acesso único ao map para poder adicionar o leilao
        lockLeiloes.writeLock();
            leiloes.put(leilaoAux.getId(), leilaoAux);
        lockLeiloes.writeUnlock();
        
        return leilaoAux;
    }
    
    /*
    * Função para licitar em leilão
    */
    public boolean licitaLeilao(Leilao leiA, float value, Utilizador user) throws InterruptedException{
        boolean resultado;
        resultado = leiA.licita(value, user);
        return resultado;
    }
    
    /*
    * Função para fechar leilão e notifica os utilizador apartir das Threads de mensagens
    */
    public boolean fechaLeilao(Leilao leiA, Utilizador user) throws InterruptedException{
        boolean resultado = false;
        ArrayList<Utilizador> licitadores;
        if(leiA.getVendedor().equals(user)){
            leiA.fechaLeilao();
            String s = "[SYSTEM] O vencedor do leilao «" + leiA.getId() + "» foi {" + 
                leiA.getMelhorLicitador().getUsername() + "} com licitação de |" + leiA.getValor() + "€|";
            licitadores = leiA.getLicitadores();
            licitadores.add(0, user);
            notificaUtilizadores(licitadores, s);
        }
        return resultado;
    }
    
    public void notificaUtilizadores(ArrayList<Utilizador> us, String s) throws InterruptedException{
        //Por cada licitador, vai à Thread de mensagens, adiciona a mensagens de fim de leilão
        // e acorda as Threads para entregar as mensagens;
        lockQuadro.readLock();
                for(int i = 0; i<us.size();i++){
                    us.get(i).addMessage(s);
                    // Verifica se o utilizador está logado no sistema para enviar notificação.
                    if(quadroMensagens.containsKey(us.get(i).getUsername()))
                        quadroMensagens.get(us.get(i).getUsername()).acorda();
                }
        lockQuadro.readUnlock();
    }
    
    /*
    * Função para presistir a "base de dados" de utilizadores num ficheiro
    */
    public boolean presisteUtilizadores() throws IOException, InterruptedException{
        lockUtilizadores.readLock();
            Write writer = new Write(utilizadores);
            //Escreve para um ficheiro os utilizadores
            writer.writeToFile("utilizadores.ld");
        lockUtilizadores.readUnlock();
        return true;
    }
    
    /*
    * Função para presistir a "base de dados" de utilizadores num ficheiro
    */
    public boolean presisteLeiloes() throws IOException, InterruptedException{
        lockLeiloes.readLock();
            Write writer = new Write(leiloes);
            //Escreve para um ficheiro os utilizadores
            writer.writeToFile("leiloes.ld");
        lockLeiloes.readUnlock();
        return true;
    }
}
