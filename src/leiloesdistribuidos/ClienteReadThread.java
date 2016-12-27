package leiloesdistribuidos;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
/**
 * Lê de um socket dado e escreve-os no standart output.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ClienteReadThread extends Thread
{
    // instance variables - replace the example below with your own
    private Socket s;
    private BufferedReader br;

    /**
     * Constructor for objects of class ClienteThreadRead
     */
    public ClienteReadThread(Socket s, BufferedReader br)
    {
       this.s = s;
       this.br = br;
    }

    
    public void run(){
       String read = null;
       while( !s.isClosed() ){
           try{
               while( (read = br.readLine()) != null){
                   System.out.println(read);
                }
           }catch(IOException e){
            }
        }
        
       
    }
}
