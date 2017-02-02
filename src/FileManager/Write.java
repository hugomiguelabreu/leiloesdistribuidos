/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
/**
 *
 * @author markerstone
 */
public class Write {
    
    Object dados;
    
    /*
    * Construtor da classe Writer que recebe o objeto genérico a escrever;
    */
    public Write(Object mapParam){
        dados = mapParam;
    }
    
    /*
    * Função que escreve um objeto genérico para um ficheiro;
    */
    public boolean writeToFile(String fileName) throws FileNotFoundException, IOException{
        
        FileOutputStream fos = new FileOutputStream(fileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(dados);
        }
        
        return true;
    }
    
}
