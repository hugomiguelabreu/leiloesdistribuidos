/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author markerstone
 */
public class Cliente {
    
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 6063);
        PrintWriter writeToServer = new PrintWriter(client.getOutputStream(), true);
        InputStreamReader k = new InputStreamReader(client.getInputStream());
        BufferedReader readFromServer = new BufferedReader(k);
        ClientReadThread ct = new ClientReadThread(readFromServer);
        
        Scanner scanner = new Scanner(System.in);
        ct.start();
        while(true){
            String str = scanner.nextLine();
            if(str.compareTo("9")==0) break;
            writeToServer.println(str);
        }
        client.shutdownOutput();
    }
    
}
