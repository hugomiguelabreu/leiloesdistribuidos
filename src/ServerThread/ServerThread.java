/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import leiloesdistribuidos.Leilao;
import leiloesdistribuidos.LeiloesDistribuidos;
import leiloesdistribuidos.Utilizador;

/**
 *
 * @author markerstone
 */
public class ServerThread extends Thread {
    private Socket clientSocket;
    private InputStreamReader k;
    private BufferedReader readFromClient;
    private PrintWriter writeToClient;
    private boolean userLoggedIn;
    private Utilizador user;
    private LeiloesDistribuidos ld;
    
    /*
    * Construtor da classe
    */
    public ServerThread(Socket paramS, LeiloesDistribuidos ld) throws IOException{
        clientSocket = paramS;
        userLoggedIn = false;
        this.ld = ld;
        k = new InputStreamReader(this.clientSocket.getInputStream());
        //Cria o canal de leitura do cliente
        readFromClient = new BufferedReader(k);
        //Cria o canal de escrita para o cliente
        writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    
    /**
     *
     * Função que executa aquando da iniciação da Thread;
     * 
     */
    @Override
    public void run(){
        String m;
        try {
            //Imprime o menu do servidor para o client;
            this.imprimeMenu();
            //Leitura do canal;
            while((m=readFromClient.readLine())!=null){
                try{
                    //Intrepertadores diferentes caso o utilizador está logado ou não;
                    if(!userLoggedIn)
                        loggedOutInterpreter(Integer.parseInt(m));
                    else
                        loggedInInterpreter(Integer.parseInt(m));
                    //No final de cada interpretação é impresso o menu para o client;
                    this.imprimeMenu();
                } catch(NumberFormatException e) {
                    writeToClient.println("Erro de input"); 
                }
            }
            //Caso o canal seja fechado pelo client, e houver um utilizador logado, faz o logout;
            if(userLoggedIn)
                this.logoutUtilizador();
            ld.presisteLeiloes();
            ld.presisteUtilizadores();
            //Fecha o socket;
            clientSocket.close();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /*
    *
    * Função de impressão do menu para o client;
    * Existem menus diferentes caso o utilizador está logado ou não;   
    */
    public void imprimeMenu(){
        writeToClient.println("--------------------------");        
        if(!userLoggedIn){
            //Menu para utilizador não logado
            writeToClient.println("|[1]Registar utilizador  |");
            writeToClient.println("|[2]Efetuar login        |");
        }else{
            //Menu para utilizador logado
            writeToClient.println("|[1]Ver leilões em curso |");
            if(!this.user.getTipo()){
                writeToClient.println("|[2]Criar leilão         |");
                writeToClient.println("|[3]Encerrar leilão      |");    
            }else{
                writeToClient.println("|[2]Licitar em leilão    |");
            }
            writeToClient.println("|[7]Logout               |");
        }
        writeToClient.println("|[9]Sair                 |");
        writeToClient.println("--------------------------");
    }
    
    /*
    *Intrepertador para o menu de utilizador não logado
    */
    public void loggedOutInterpreter(int opt) throws IOException, InterruptedException{
        //Switch para interpretar a opção recebida pelo client;
        switch(opt){
            //Registar
            case 1:
                writeToClient.println("Username:");
                String username = readFromClient.readLine();
                writeToClient.println("Password:");
                String password = readFromClient.readLine();
                writeToClient.println("[1]Comprador ou [2]Vendedor:");
                int tipo = Integer.parseInt(readFromClient.readLine());
                writeToClient.println((this.registaUtilizador(username, password, (tipo==1 ? true : false)) ? "Registo efetuado com sucesso" : "Erro no registo"));
                break;
            //Login
            case 2:
                writeToClient.println("Username:");
                String usernameLogin = readFromClient.readLine();
                writeToClient.println("Password:");
                String passwordLogin = readFromClient.readLine();
                writeToClient.println((this.loginUtilizador(usernameLogin, passwordLogin) ? "Login efetuado com sucesso" : "Username ou password errados"));
                break;
            //Sair
            case 9:
                clientSocket.close();
                break;
            default:
                writeToClient.println("Opção inválida");
                break;
        }
    }
    
    /*
    * Intrepertador para o menu de utilizador logado
    */
    public void loggedInInterpreter(int opt) throws IOException, InterruptedException{
        switch(opt){
            case 1:
                ArrayList<Leilao> leiloes = ld.getLeiloes();
                for(int i = 0; i<leiloes.size(); i++){
                    if(leiloes.get(i).getEstado()==true){
                        writeToClient.println("__________________________________________________________________________");
                        writeToClient.println("[" + leiloes.get(i).getId() + "] " + leiloes.get(i).getDesc() + " -> " + leiloes.get(i).getValor() + "€ " 
                            + (leiloes.get(i).getVendedor().equals(this.user) ? " (*)" : ((leiloes.get(i).getMelhorLicitador() != null) && 
                                        (leiloes.get(i).getMelhorLicitador().equals(this.user)) ? " (+)" : "")));
                    }
                }
                writeToClient.println("__________________________________________________________________________");
                break;
            case 2:
                if(!this.user.getTipo()){
                    writeToClient.println("Descrição:");
                    String descLeilao = readFromClient.readLine();
                    writeToClient.println("Valor:");
                    float valLeilao = Float.parseFloat(readFromClient.readLine());
                    Leilao leilaoAux = this.criaLeilao(valLeilao, descLeilao);
                    writeToClient.println("Id do leilão:" + leilaoAux.getId());
                }else{
                    writeToClient.println("Id do leilão:");
                    String idLeilao = readFromClient.readLine();
                    if(ld.existeLeilao(idLeilao)){
                        Leilao leiAux = ld.getLeilao(idLeilao);
                        if(leiAux.getEstado()){
                            writeToClient.println("O valor atual do leilão é: " + leiAux.getValor());
                            writeToClient.println("Licitação:");
                            float valLicit = Float.parseFloat(readFromClient.readLine());
                            writeToClient.println((this.licitaLeilao(leiAux, valLicit)) ? "Licitação sucedida" : "Licitação inválida");
                        }
                    }else{
                        writeToClient.println("O leilão não existe ou já terminou");
                    }
                }
                break;
            case 3:
                if(!this.user.getTipo()){
                    writeToClient.println("Id do leilão a fechar:");
                    String idLei = readFromClient.readLine();
                    if(ld.existeLeilao(idLei)){
                        Leilao aLei = ld.getLeilao(idLei);
                        ld.fechaLeilao(aLei, this.user);
                    }else{
                        writeToClient.println("Leilão inexistente.");
                    }
                }
                break;
            case 7:
                writeToClient.println(this.logoutUtilizador() ? "Até breve!" : "Error!");
                break;
            case 9:
                clientSocket.close();
                break;
            default:
                writeToClient.println("Opção inválida");
                break;
        }
    }
    
    /*
    * Função para registar um utilizador no sistema
    */
    public boolean registaUtilizador(String username, String password, boolean tipo) throws IOException, InterruptedException{
        return ld.registaUtilizador(username, password, tipo);
    }
    
    /*
    * Função para fazer o login de um utilizador no sistema;
    */
    public boolean loginUtilizador(String username, String password) throws InterruptedException, IOException{
        boolean resultado=false;
        if(ld.loginUtilizador(username, password, clientSocket)){
            this.userLoggedIn = true;
            this.user = ld.getUtilizador(username);
            resultado = true;
        }
        return resultado;
    }
    
    /*
    * Faz o logout do utilizador;
    */
    public boolean logoutUtilizador() throws InterruptedException{
        if(ld.logoutUtilizador(this.user))
            userLoggedIn = false;
        return true;
    }
    
    /*
    * Cria um leilão;
    */
    public Leilao criaLeilao(float value, String desc) throws IOException, InterruptedException{
        if(!this.user.getTipo())
            return ld.criaLeilao(value, desc, this.user);
        else
            return null;
    }

    /*
    * Licita num dado leilão;
    */
    public boolean licitaLeilao(Leilao leiA, float value) throws InterruptedException{
        return ld.licitaLeilao(leiA, value, this.user);
    }
}
