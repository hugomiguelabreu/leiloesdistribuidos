/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;

import ServerThread.ServerThread;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author markerstone
 */
public class Servidor {
    
    /**
     * Função main do servidor
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        ServerSocket s;
        Socket c;
        ServerThread sThread;    
        LeiloesDistribuidos ld;
        
        //Inicia o centro de leilões;
        ld = new LeiloesDistribuidos();
        //Inicia o seridor na porta 6063
        s = new ServerSocket(6063);
        System.out.println("Servidor de leilões iniciado.");
        //Espera pelos clientes
        while((c=s.accept())!=null){
            //Inicia uma Thread para um cliente
            sThread = new ServerThread(c, ld);
            sThread.start();
        }
        s.close();
        
    }
 
}