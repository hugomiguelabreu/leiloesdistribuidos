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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Olá, bem-vindo ao cliente de LeilõesDistribuidos! :)");
        System.out.println("Qual o endereço do servidor ao qual te pretendes ligar?");
        System.out.print("Endereço:");
        String ip = scanner.nextLine();
        System.out.print("Porto:");
        String port = scanner.nextLine();
        try{
            Socket client = new Socket(ip, Integer.parseInt(port));
            PrintWriter writeToServer = new PrintWriter(client.getOutputStream(), true);
            InputStreamReader k = new InputStreamReader(client.getInputStream());
            BufferedReader readFromServer = new BufferedReader(k);
            ClientReadThread ct = new ClientReadThread(readFromServer);

            //Inicia a thread de leitura;
            ct.start();
            while(true){
                String str = scanner.nextLine();
                //Caso seja a opção de saida, sai do ciclo;
                if(str.compareTo("9")==0) break;
                writeToServer.println(str);
            }
            client.shutdownOutput();
        }catch(IOException ex){
            System.out.println("Servidor está inacessível. Exiting...");
        }
      }  
}
