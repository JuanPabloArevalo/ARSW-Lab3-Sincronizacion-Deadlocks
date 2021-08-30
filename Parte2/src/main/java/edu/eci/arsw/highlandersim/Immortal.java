package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Immortal extends Thread {

    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    boolean pause = false;
    private boolean gamEjecutandose = true;
    
    private final Object lock;

    public void pause() {
        pause = true;
    }

   

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, Object lock) {
        super(name);
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.lock = lock;
    }

    public void run() {

        while (gamEjecutandose) {
            if(!pause){
                Immortal im;
                int myIndex = immortalsPopulation.indexOf(this);
                int nextFighterIndex = r.nextInt(immortalsPopulation.size());
                //avoid self-fight= Evitar la auto-lucha
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }
                //contra el que se va luchar
                im = immortalsPopulation.get(nextFighterIndex);
                this.fight(im);
                try {
                   Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } 
            else{
                synchronized(lock){
                    try {
                         //Mientras que los inmortales estan pausados todos se van a dormir 
                         lock.wait();
                     } catch (InterruptedException ex) {
                         Logger.getLogger(Immortal.class.getName()).log(Level.SEVERE, null, ex);
                     }
                }     
            }
        
        }

    }
     public void cont() {
         //despiesta a los inmortales
        if(pause == true){
            pause = false;
            synchronized(lock){
                lock.notify();
            }
        }
        
    }
   
    //Aqui luchan los inmortales
    public void fight(Immortal i2) {
        Object key1 , key2;
        //Aqui se le da un orden a las llaves, dando las opciones que se si entra priimero un
        // que el otro
        if(immortalsPopulation.indexOf(this) > immortalsPopulation.indexOf(i2)){
            
            key1 = lock;
            key2 = i2.lock;   
        
        }else{
            key1 = lock;
            key2 = i2.lock; 
        }
        
        synchronized(key1){
            synchronized(key2){
            
                if (i2.getHealth() > 0) {
                    //Aqui se le suma vida a this y se le resta a i2 
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    System.out.println("Fight: " + this + " vs " + i2);
                } else {
                    System.out.println(this + " says:" + i2 + " is already dead!");
                }
            }
        }
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

   public void finishGame(){
       gamEjecutandose = false;
       
   }
}
