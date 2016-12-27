package leiloesdistribuidos;


/**
 * Write a description of class Test here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Test
{
    public void main(){
        Servidor s = new Servidor();
        Cliente c = new Cliente();
        
        Thread a = new Thread(){
                    @Override
                    public void run(){
                        try{
                        s.main(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    }
                };
        Thread b = new Thread(){
                    @Override
                    public void run(){
                        c.main(null);
                    }
                };
                
        a.start();
        try{
        this.wait(1);
        }catch(Exception e){
        }
        b.start();
        
    }
}
