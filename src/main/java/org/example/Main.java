package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Main {
    public static void main(String[] args) throws IOException{
        try(ServerSocket serverSocket = new ServerSocket(4221)) {
            ExecutorService pool = Executors.newFixedThreadPool(100);

            serverSocket.setReuseAddress(true);
            System.out.println("Server listening on port: "+4221);
            while (true) {
                Socket client = serverSocket.accept(); // Wait for connection from client.
                pool.execute(new Server(client));
               // server.start(); 
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}