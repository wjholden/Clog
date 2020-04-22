package com.wjholden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class Client implements Runnable {
    private final Page mainPage;

    private final Socket socket;

    public Client(Socket socket, Page mainPage) {
        this.socket = socket;
        this.mainPage = mainPage;
    }

    @Override
    public void run() {
        var deque = new LinkedList<Page>();
        deque.addLast(mainPage);

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (deque.size() > 0) {
                final var currentPage = deque.getLast();
                out.print("\u001B[2J");
                out.println(currentPage);
                Page page;

                if (currentPage.characterMode) {
                    final char c = (char) in.read();
                    page = currentPage.command(c);
                } else {
                    final String s = in.readLine();
                    page = currentPage.command(s);
                }

                if (page == null) {
                    deque.removeLast();
                } else if (page != currentPage) {
                    deque.addLast(page);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
