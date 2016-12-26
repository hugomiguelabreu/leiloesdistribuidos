/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistibuidos;

import FileManager.Read;
import ServerThread.ServerThread;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author markerstone
 */
public class Servidor {
    
    private static Map<String, Utilizador> utilizadores;
    private static Map<String, Leilao> leiloes;
    
    /**
     * Função main do servidor
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException {
        ServerSocket s;
        Socket c;
        ServerThread sThread;
        Read rd = new Read();
        File file = new File("utilizadores.ld");
        File fileLeiloes = new File("leiloes.ld");
        //Inicia o HashMap de Utilizadores
        utilizadores = new HashMap<String, Utilizador>();// Modificado por victor, Precisei o tipo de chave
        leiloes = new HashMap<String,Leilao>();
        // e objecto guardado no HashMap.
        //Verifica se existe o ficheiro que contém os utilizadores
        if(file.exists())
            utilizadores = (HashMap<String,Utilizador>)rd.readFromFile("utilizadores.ld");
        //Verifica se existe o ficheiro que contém os leiloes
        if(fileLeiloes.exists())
            leiloes = (HashMap<String,Leilao>)rd.readFromFile("leiloes.ld");
        
            
        for(Utilizador us:utilizadores.values())
            System.out.println(us.getUsername());
        
        //Inicia o seridor na porta 6063
        s = new ServerSocket(6063);
        //Espera pelos clientes
        while((c=s.accept())!=null){//O servidor fica a espera at� obter um cliente.
            // O servidor nunca para de esperar. accept bloqueia at� obter um cliente
            //Inicia uma Thread para um cliente
            sThread = new ServerThread(c, utilizadores);
            sThread.start();
        }
        s.close();
        
    }
 
}