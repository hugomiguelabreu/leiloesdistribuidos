package leiloesdistibuidos;

import java.io.Serializable;
/**
 * Write a description of class CompleteId here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class CompleteId implements Serializable
{
    // instance variables - replace the example below with your own
    private static int x = 0;


    public CompleteId(){
    }

    synchronized public static int getNextId(){
        return x++;
    }
}
