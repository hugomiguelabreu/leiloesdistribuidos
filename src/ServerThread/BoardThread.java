/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerThread;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import leiloesdistribuidos.Utilizador;

/**
 *
 * Esta é a Thread de mensagens, encarregue por entregar mensagens
 * aos clients;
 *
 */
public class BoardThread extends Thread{
    
    private Socket clientSocket;
    private PrintWriter writeToClient;
    private Utilizador user;
    
    // Queue de mensagens a escrever para o cliente;
    
    /*
    * Construtor da classe BoardThread;
    */
    public BoardThread(Socket paramS, Utilizador user) throws IOException{
        this.user = user;
        this.clientSocket = paramS;
        //Cria o canal de escrita para o cliente
        this.writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    
    /**
     * Função que corre a thread
     */
    @Override
    public synchronized void run(){
        try {
            while(clientSocket.isConnected()){
                //Caso a thread acorde do wait() e não tiver nada na queue para escrever
                //volta a dormir;
                while(!user.hasMessages()){
                    wait();
                }
                //Escreve para o client a mensagem do topo da queue;
                writeToClient.println(user.getMessage());
            }
            clientSocket.close();
            
        } catch (InterruptedException | IOException ex) {
        }
    }

    /*
    * Função que acorda todas as threads que estão a dormir;
    * Quando esta função é chamada é porque existe mensagens para entregar;
    */
    public synchronized void acorda(){
        notifyAll();
    }
    
}
