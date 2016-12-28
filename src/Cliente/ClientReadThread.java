/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author markerstone
 */
public class ClientReadThread extends Thread{
    
    BufferedReader readFromServer;
    
    public ClientReadThread(BufferedReader readFromServer) throws IOException{
        this.readFromServer = readFromServer;
    }
    
    public void run(){
        String m;
        try {
            while((m=readFromServer.readLine()) != null){
                System.out.println(m);
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientReadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
