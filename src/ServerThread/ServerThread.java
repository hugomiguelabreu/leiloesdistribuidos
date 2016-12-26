/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThread;

import FileManager.Write;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import leiloesdistibuidos.Leilao;
import leiloesdistibuidos.Utilizador;

/**
 *
 * @author markerstone
 */
public class ServerThread extends Thread {
    private Socket clientSocket;
    private InputStreamReader k;
    private BufferedReader readFromClient;
    private PrintWriter writeToClient;
    private Map<String, Utilizador> utilizadores;
    private Map<String, Leilao> leiloes;
    private boolean userLoggedIn;
    private Utilizador user;
    
    /*
    * Construtor da classe
    */
    public ServerThread(Socket paramS, Map utilizadoresParam, Map leiloesParam) throws IOException{
        clientSocket = paramS;
        //Guarda os apontadores para os maps (ou seja a base de dados em memoria
        utilizadores = utilizadoresParam;
        leiloes = leiloesParam;
        userLoggedIn = false;
        
        k = new InputStreamReader(this.clientSocket.getInputStream());
        //Cria o canal de leitura do cliente
        readFromClient = new BufferedReader(k);
        //Cria o canal de escrita para o cliente
        writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    
    public void run(){
        String m = null;
        try {
            this.imprimeMenu();
            while((m=readFromClient.readLine())!=null){
                if(!userLoggedIn)
                    loggedOutInterpreter(Integer.parseInt(m));
                else
                    loggedInInterpreter(Integer.parseInt(m));
                this.imprimeMenu();
            }
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void imprimeMenu(){
        
        if(!userLoggedIn){
            //Menu para utilizador não logado
            writeToClient.println("[1]Registar utilizador");
            writeToClient.println("[2]Efetuar login");
        }else{
            //Menu para utilizador logado
            writeToClient.println("[1]Ver leilões em curso");
            if(!this.user.getTipo()){
                writeToClient.println("[2]Criar leilão");
                writeToClient.println("[3]Encerrar leilão");
            }else{
                writeToClient.println("[2]Licitar em leilão");
            }
            writeToClient.println("[7]Logout");
        }
    }
    /*
    *Intrepertador para menu de utilizador não logado
    */
    public void loggedOutInterpreter(int opt) throws IOException{
        switch(opt){
            case 1:
                writeToClient.println("Username:");
                String username = readFromClient.readLine();
                writeToClient.println("Password:");
                String password = readFromClient.readLine();
                writeToClient.println("[1]Comprador ou [2]Vendedor:");
                int tipo = Integer.parseInt(readFromClient.readLine());
                writeToClient.println((this.registaUtilizador(username, password, (tipo==1 ? true : false)) ? "Registo efetuado com sucesso" : "Erro no registo"));
                break;
            case 2:
                writeToClient.println("Username:");
                String usernameLogin = readFromClient.readLine();
                writeToClient.println("Password:");
                String passwordLogin = readFromClient.readLine();
                writeToClient.println((this.loginUtilizador(usernameLogin, passwordLogin) ? "Login efetuado com sucesso" : "Username ou password errados"));
                break;
            case 3:
                break;
            default:
                writeToClient.println("Opção inválida");
                break;
        }
    }
    
    /*
    * Intrepertador para o menu de utilizador logado
    */
    public void loggedInInterpreter(int opt) throws IOException{
        switch(opt){
            case 1:
                for(Leilao leiAux : leiloes.values()){
                    writeToClient.println("__________________________________________________________________________");
                    writeToClient.println("[" + leiAux.getId() + "] " + leiAux.getDesc() + " -> " + leiAux.getValor() + "€ " 
                            + (leiAux.getVendedor().getUsername().equals(
                                    this.user.getUsername()) ? " (*)" : ((leiAux.getMelhorLicitador() != null) && (leiAux.getMelhorLicitador().equals(
                                            this.user.getUsername())) ? " (+)" : "")));
                }
                writeToClient.println("__________________________________________________________________________");
                break;
            case 2:
                if(this.userLoggedIn){
                    writeToClient.println("Descrição:");
                    String descLeilao = readFromClient.readLine();
                    writeToClient.println("Valor:");
                    float valLeilao = Float.parseFloat(readFromClient.readLine());
                    Leilao leilaoAux = this.criaLeilao(valLeilao, descLeilao);
                    writeToClient.println("Id do leilão:" + leilaoAux.getId());
                }else{
                    writeToClient.println("Id do leilão:");
                    String idLeilao = readFromClient.readLine();
                    Leilao leiAux = leiloes.get(idLeilao);
                    writeToClient.println("O valor atual do leilão é: " + leiAux.getValor());
                    writeToClient.println("Licitação:");
                    float valLicit = Float.parseFloat(readFromClient.readLine());
                    writeToClient.println((this.licitaLeilao(leiAux, valLicit)) ? "Licitação sucedida" : "Licitação inválida");
                }
                break;
            case 7:
                writeToClient.println(this.logoutUtilizador() ? "Até breve!" : "Error!");
                break;
            default:
                writeToClient.println("Opção inválida");
                break;
        }
    }
    
    /*
    * Função para fazer o login de um utilizador no sistema (Aqui pode executar concorrentemente
    */
    public boolean loginUtilizador(String username, String password){
        Utilizador userAux;
        boolean resultado = false;
        //Verifica se o utilizador e password estão corretos   
        if(utilizadores.containsKey(username)){
           userAux = utilizadores.get(username);
           //Verifica se a password é correta
           if(userAux.getPassword().equals(password)){
                this.user = userAux;
                this.userLoggedIn = true;
                resultado = true;
            }
        }
        return resultado;
    }
    /*
    * Faz o logout do utilizador
    */
    public boolean logoutUtilizador(){
        if(userLoggedIn)
            userLoggedIn = false;
        return true;
    }
    
    /*
    * Função para registar um utilizador no sistema
    */
    public boolean registaUtilizador(String username, String password, boolean tipo) throws IOException{
        boolean resultado=false;
        //Verifica se o utilizador já existe
        if(!utilizadores.containsKey(username)){
            //Cria o utilizador
            Utilizador user = new Utilizador(username, password, tipo);
            //Acesso único ao map para poder adicionar o utilizador
            synchronized(utilizadores){
                utilizadores.put(username, user);
            }
        }
        
        return resultado;
    }

    /*
    * Função para registar um leilao no sistema
    */
    public Leilao criaLeilao(float value, String desc) throws IOException{
        
        //Cria o utilizador
        Leilao leilaoAux = new Leilao(value, desc, this.user);
        //Acesso único ao map para poder adicionar o utilizador
        synchronized(leiloes){
            leiloes.put(leilaoAux.getId(), leilaoAux);
        }
        
        return leilaoAux;
    }
    
    /*
    * Função para licitar em leilão
    */
    
    public boolean licitaLeilao(Leilao leiA, float value){
        boolean resultado = false;
        resultado = leiA.licita(value, this.user);
        if(resultado)
            this.user.addLeilao(leiA);
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
