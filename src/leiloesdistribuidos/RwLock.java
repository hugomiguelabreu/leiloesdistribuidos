/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;
/**
 *
 * @author markerstone
 */
public class RwLock {
    
    private int writers,
            readers,
            rflw,//rflw é o numero de leituras depois da ultima escrita
            iw; // iw itenção de escrita
    
    public RwLock(){
        writers = 0;
        readers = 0;
        iw = 0;
        rflw = 0;
    }
    
    public synchronized void readLock() throws InterruptedException{
        while(writers!=0 || (rflw >= 20 && iw!=0))
            wait();
        readers++;
    }
    
    public synchronized void readUnlock(){
        rflw++;
        readers--;
        notifyAll();
    }
    
    public synchronized void writeLock() throws InterruptedException{
        iw++;
        while(readers!=0 || writers!=0)
            wait();
        writers++;
        iw--;
        rflw=0;
    }
    
    public synchronized void writeUnlock(){
        writers--;
        notifyAll();
    }
    
}
