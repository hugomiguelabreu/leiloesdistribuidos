/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author markerstone
 */
public class ClientReadThread extends Thread{
    
    BufferedReader readFromServer;
    
    /*
    * Construtor da thread, que recebe o canal de leitura do servidor;
    */
    public ClientReadThread(BufferedReader readFromServer) throws IOException{
        this.readFromServer = readFromServer;
    }
    
    /**
     * Função que corre quando a thread é iniciada;
     * Esta função é a de leitura do canal do servidor;
     */
    @Override
    public void run(){
        String m;
        try {
            //Lê do canal enquanto ouver algo para ler;
            while((m=readFromServer.readLine()) != null){
                System.out.println(m);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientReadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
