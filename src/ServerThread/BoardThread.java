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
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import leiloesdistribuidos.LeiloesDistribuidos;
import leiloesdistribuidos.Utilizador;

/**
 *
 * @author markerstone
 */
public class BoardThread extends Thread{
    
    private Socket clientSocket;
    private InputStreamReader k;
    private BufferedReader readFromClient;
    private PrintWriter writeToClient;
    private Utilizador user;
    private Queue<String> messages;
    private boolean fechaClient;
    
    /*
    * Construtor da classe
    */
    public BoardThread(Socket paramS) throws IOException{
        clientSocket = paramS;
        messages = new LinkedList<String>();
        //Cria o canal de escrita para o cliente
        writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    
    public synchronized void run(){
        try {
            while(clientSocket.isConnected()){
                while(messages.isEmpty()){
                    wait();
                }
                writeToClient.println(messages.remove());
            }
            clientSocket.close();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(BoardThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BoardThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sethasMessages(String s){
        messages.add(s);
    }
    
    public synchronized void acorda(){
        notifyAll();
    }
    
}
