package org.sgh.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClientExample {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        Socket socket;
        String msg = "";
        while (!msg.equals("exit")) {
            socket = new Socket("192.168.178.113", 9875);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            msg = in.readLine();
            System.out.println(msg);
        }
        System.out.println("I'm going to die...");
    }
}
