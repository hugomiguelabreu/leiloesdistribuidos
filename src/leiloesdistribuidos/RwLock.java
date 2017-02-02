/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leiloesdistribuidos;

import java.io.Serializable;

/**
 *
 * @author markerstone
 */
public class RwLock implements Serializable{
    
    private int writers,
            readers,
            rflw,//rflw é o numero de leituras depois da ultima escrita
            iw; // iw itenção de escrita
    /*
    * Construtor do ReadWriteLock
    */
    public RwLock(){
        writers = 0;
        readers = 0;
        iw = 0;
        rflw = 0;
    }
    
    /*
    * Bloqueia os writers, pois estão a ler;
    */
    public synchronized void readLock() throws InterruptedException{
        while(writers!=0 || (rflw >= 20 && iw!=0))
            wait();
        readers++;
    }
    
    /*
    * Desbloqueia os writers, pois já não estão a ler;
    */
    public synchronized void readUnlock(){
        rflw++;
        readers--;
        notifyAll();
    }
    
    /*
    * Bloqueia os readers, pois estão a escrever;
    */
    public synchronized void writeLock() throws InterruptedException{
        iw++;
        while(readers!=0 || writers!=0)
            wait();
        writers++;
        iw--;
        rflw=0;
    }
    
    /*
    * Desbloqueia os readers, pois já não estão a escrever;
    */
    public synchronized void writeUnlock(){
        writers--;
        notifyAll();
    }
    
}
