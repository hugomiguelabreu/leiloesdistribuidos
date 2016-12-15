/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author markerstone
 */
public class Read {
    
    public Read(){
    }
    
    public Object readFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
        
      FileInputStream fin = new FileInputStream(fileName);
      ObjectInputStream ois = new ObjectInputStream(fin);
      return ois.readObject();
    }
    
}
