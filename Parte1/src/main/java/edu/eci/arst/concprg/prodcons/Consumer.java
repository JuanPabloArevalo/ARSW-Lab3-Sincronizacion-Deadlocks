/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    
    private Queue<Integer> queue;
    private Object lock;
    
    public Consumer(Queue<Integer> queue, Object lock){
        this.queue=queue;       
        this.lock=lock;
    }
    
    @Override
    public void run() {
        while (true) {
            //Si hay cantidades disponibles para consumir
            if (queue.size() > 0) {
                //Consume
                int elem=queue.poll();
                System.out.println("Consumer consumes "+elem);  
                //Despierta al productor.
                synchronized(lock){
                    lock.notify();
                }
                //Hace una espera
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //Si no hay cantidades disponibles para consumir
            else{
                //Despierta al productor.
                synchronized(lock){
                    lock.notify();
                }
                //Se duerme
                synchronized(lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            
        }
    }
}
