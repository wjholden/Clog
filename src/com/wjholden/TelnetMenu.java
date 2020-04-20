package com.wjholden;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class TelnetMenu {

    private static final String OPTIONS[] = new String[] {
            "PAGE_UP", "PAGE_DOWN", "", "", "",
            "", "", "", "", "EXIT"
    };

    public static void telnet() {
        //int[] data = IntStream.range(1, 101).toArray();
        double[] data = DoubleStream.generate(Math::random).limit(100).toArray();

        try (ServerSocket serverSocket = new ServerSocket(23);
             Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            int offset = 0;

            // Option negotiation for PuTTY.
            // This all magically works in Microsoft's telnet CLI program.

            out.print(255); // IAC (interpret as command)
            out.print(253); // DO (do this)
            out.print(31);  // Window Size
            out.print(250); // SB (sub-negotiation)
            out.print(0);
            out.print(80);
            out.print(0);
            out.print(24);
            out.print(240); // SE (end of sub-negotiation)

            // todo: looks like this is out of order. We should read options from client first.

            running:
            while (true) {
                out.println("TelnetMenu.java");
                out.println("Main Menu");
                out.println();
                for (int i = 0 ; i < 18 ; i++) {
                    out.println(String.format("%3d: ", 1 + i + offset) + data[i + offset]);
                }
                out.println();
                printOptions(OPTIONS, out);

                final char input = (char) in.read();

                out.print("\u001B[2J");
                System.out.println((int) input);
                switch (input) {
                    case '0' + 1:
                        offset = Math.max(0, offset - 10);
                        break;
                    case '0' + 2:
                        offset = Math.min(data.length - 18, offset + 10);
                        break;
                    case '0' + 0:
                        break running;
                    case 24:
                        offset = Math.max(0, offset - 1);
                        break;
                    case 25:
                        offset = Math.min(data.length - 18, offset + 1);
                        break;
                }
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    private static void printOptions(String[] options, PrintWriter out) {
        for (int i = 0 ; i < options.length ; i++) {
            if (i == options.length / 2) {
                out.println();
            }
            out.print(String.format("%d-%-12.12s  ", (i + 1) % 10, options[i]));
        }
        out.flush();
    }
}
