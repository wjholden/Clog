package com.wjholden;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Menu {

    public Menu(int port, Page mainPage) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new Client(clientSocket, mainPage));
            }
        } catch (IOException ex) {
                System.err.println(ex);
        }
    }

}
