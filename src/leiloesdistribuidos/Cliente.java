/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;
import java.net.Socket;
import java.util.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
/**
 * Cliente que fala / speak com o servidor.
 * @author markerstone
 */
public class Cliente {
    /*
    *private Socket s = null;
    private ClienteReadThread crt = null;
    private final int port = 6062;
    
    public Cliente(){
    }
    */
    
    public static void main(String[] args){
        Socket s = null;
        ClienteReadThread crt = null;
        int port = 6062;
        /* Tenta estabelecer uma conexao com o servidor*/
        try{
            s = new Socket("localhost",port);
        }catch(Exception e ){
            System.out.println("Nenhuma connexao possivel com o servidor.\nTeste a sua conexão.");
        }
        if(s == null)
                    return;
        
        /* Agora que uma connexao foi estabelecida vamos falar com o servidor. */
        
        BufferedReader br  = null;
        PrintWriter pw = null;
        
        try{
            br = new BufferedReader( new InputStreamReader( s.getInputStream() ) ) ;
            pw = new PrintWriter( s.getOutputStream(),true);
        }catch(Exception e ){
                System.out.println("Um problema ocorreu a tentar estabelecer uma connexao com o servidor."); 
        }
        if( pw == null && br == null)
                    return;
        /* Esta thread so vai ler mensagens do servidor*/
        crt = new ClienteReadThread(s,br);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        crt.start();
        
        String readed = null;
        /* Vou lendo até que o utilizador quer sair */
        try{
        while( (readed = in.readLine()) != null && readed.equals("Quit")== false){
            pw.println(readed);
        }
        }catch(IOException e){
        System.out.println("A connexao foi interronpida.");
        }
        
        
        
        
        /* Quando sair temos que fechar a conexao se ainda nao esta fechada. */
        try{
        if( !s.isClosed() )
                        s.close();      
        }catch(Exception e) {};
        try{
        in.close();
        }catch(Exception e){};
    }
    
    
    
}
