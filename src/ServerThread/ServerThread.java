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
import leiloesdistibuidos.Utilizador;

/**
 *
 * @author markerstone
 */
public class ServerThread extends Thread {
    Socket clientSocket;
    InputStreamReader k;
    BufferedReader readFromClient;
    PrintWriter writeToClient;
    Map<String, Utilizador> utilizadores;
    boolean userLoggedIn;
    
    /*
    * Construtor da classe
    */
    public ServerThread(Socket paramS, Map utilizadoresParam) throws IOException{
        clientSocket = paramS;
        utilizadores = utilizadoresParam;
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
                    loggedOutInterpreter(Integer.getInteger(m));
                else
                    loggedInInterpreter(Integer.getInteger(m));
                this.imprimeMenu();
            }
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void imprimeMenu(){
        
        if(!userLoggedIn){
            writeToClient.println("[1]Registar utilizador");
            writeToClient.println("[2]Efetuar login");
        }else{
            //Menu para utilizador logado
            writeToClient.println("[7]Logout");
        }
        writeToClient.println("[9]Sair");
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
                writeToClient.println("[C]omprador ou [V]endedor:");
                String tipo = readFromClient.readLine();
                writeToClient.println(this.registaUtilizador(username, password, tipo));
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }
    
    public void loggedInInterpreter(int opt){
        
    }
    
    /*
    * Função para registar um utilizador no sistema
    */
    public String registaUtilizador(String username, String password, String tipo) throws IOException{
        String resultado;
        //Verifica se o utilizador já existe    
        if(!utilizadores.containsKey(username)){
            //Cria o utilizador
            Utilizador user = new Utilizador(username, password, tipo);
            //Acesso único ao map para poder adicionar o utilizador
            synchronized(utilizadores){
                utilizadores.put(username, user);
            }
            //Presiste a base de dados num ficheiro
            resultado = presisteUtilizadores();
        }else{
            resultado = "Utilizador já existe no sistema";
        }
        return resultado;
    }
    /*
    * Função para presistir a "base de dados" de utilizadores num ficheiro
    */
    public String presisteUtilizadores() throws IOException{
        //Acesso único ao map
        synchronized(utilizadores){
            Write writer = new Write(utilizadores);
            //Escreve para um ficheiro os utilizadores
            writer.writeToFile("utilizadores.ld");
        }
        return "Registo efetuado com sucesso";
    }

}
