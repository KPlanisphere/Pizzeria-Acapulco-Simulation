package com.mycompany.pizzeriaacapulco;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PizzeriaAcapulco {

    static final int MAX_PIZZAS = 3; // Capacidad máxima de la bandeja
    static int pizzasEnBandeja = 0;

    static Lock lock = new ReentrantLock();
    static Condition repartidoresEsperando = lock.newCondition();
    static Condition cocinerosEsperando = lock.newCondition();

    static class Cocinero implements Runnable {
        @Override
        public void run() {
            while (true) {
                cocinarUnaPizza();
                lock.lock();
                try {
                    while (pizzasEnBandeja == MAX_PIZZAS) {
                        System.out.println("Bandeja llena, esperando");
                        cocinerosEsperando.await();
                    }
                    colocarPizza();
                    pizzasEnBandeja++;
                    repartidoresEsperando.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    static class Repartidor implements Runnable {
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    while (pizzasEnBandeja == 0) {
                        System.out.println("Bandeja vacía, esperando");
                        repartidoresEsperando.await();
                    }
                    retirarPizza();
                    pizzasEnBandeja--;
                    cocinerosEsperando.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
                repartirPizza();
            }
        }
    }

    // Métodos simulados
    static void cocinarUnaPizza() {
        System.out.println("Cocinando una pizza...");
        try {
            Thread.sleep(2000); // Simula tiempo de cocción
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void colocarPizza() {
        System.out.println("Pizza colocada en la bandeja");
    }

    static void retirarPizza() {
        System.out.println("Pizza retirada de la bandeja");
    }

    static void repartirPizza() {
        System.out.println("Pizza repartida al cliente");
    }

    public static void main(String[] args) {
        Thread[] cocineros = new Thread[2];
        Thread[] repartidores = new Thread[3];

        for (int i = 0; i < cocineros.length; i++) {
            cocineros[i] = new Thread(new Cocinero());
            cocineros[i].start();
        }

        for (int i = 0; i < repartidores.length; i++) {
            repartidores[i] = new Thread(new Repartidor());
            repartidores[i].start();
        }

        // Detener la ejecución tras un tiempo específico (simulación)
        try {
            Thread.sleep(15000); // Simula tiempo de ejecución
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Detener la ejecución de la pizzería
        for (Thread cocinero : cocineros) {
            cocinero.interrupt();
        }
        for (Thread repartidor : repartidores) {
            repartidor.interrupt();
        }
    }
}
