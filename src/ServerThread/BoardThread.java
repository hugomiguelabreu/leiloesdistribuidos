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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import leiloesdistibuidos.Utilizador;

/**
 *
 * @author markerstone
 */
public class BoardThread {
    Socket clientSocket;
    Map<String, Utilizador> utilizadores;
    
    public BoardThread(Socket paramS, Map<String, Utilizador> utilizadoresParam){
        clientSocket = paramS;
        utilizadores = utilizadoresParam;
    }
    
    public void run(){
   
        try {
            InputStreamReader k = new InputStreamReader(clientSocket.getInputStream());
            BufferedReader readFromClient = new BufferedReader(k);
            PrintWriter writeToClient = new PrintWriter(clientSocket.getOutputStream(), true);
            String m = null;
            while((m=readFromClient.readLine())!=null){
                if(m.equals("1")){
                    
                }
            }
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
