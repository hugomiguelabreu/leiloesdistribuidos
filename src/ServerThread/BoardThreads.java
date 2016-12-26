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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import leiloesdistibuidos.Utilizador;

/**
 *
 * @author markerstone
 */
public class BoardThreads extends Thread {
    private InputStreamReader k;
    private BufferedReader readFromClient;
    private PrintWriter writeToClient;
    private Socket clientSocket;
    private Utilizador user;
    private ReentrantLock rl;
    private Condition semNotificacoes;
    private Condition comNotificacoes;

    private boolean semNotificacoesChecker;
    private boolean master;


    
    public BoardThreads(){
        semNotificacoesChecker = true;
        master = true;
    }
    
    public BoardThreads(Socket paramS, Utilizador u) throws IOException{
        clientSocket = paramS;
        user = u;
        k = new InputStreamReader(this.clientSocket.getInputStream());
        //Cria o canal de leitura do cliente
        readFromClient = new BufferedReader(k);
        //Cria o canal de escrita para o cliente
        writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
        master = false;
    }
    
    public void run(){
        if(!master){
            rl.lock();
            try {
                while(semNotificacoesChecker)
                    semNotificacoes.await();

                //NOTIFICAR
                comNotificacoes.notifyAll();
            }   catch (InterruptedException ex) {
                    Logger.getLogger(BoardThreads.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                rl.unlock();
            }
        }
    }