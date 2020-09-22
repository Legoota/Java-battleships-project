package TP2.bn_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Scanner input;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Thread tcl;

    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        input = new Scanner(System.in);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(),true);
        // thread d'écoute
        ClientListener cl = new ClientListener(in);
        this.tcl = new Thread(cl);
        this.tcl.start();
    }

    public void execute(){
        while(this.tcl.isAlive()){ // isAlive == false si le thread n'est plus actif
            String s = input.nextLine();
            out.println(s);
        }
        close(); // fermeture du socket une fois que le thread d'écoute n'est plus actif
    }

    public void close(){
        System.out.println("Closing the connexion");
        this.input.close();
        try {
            this.in.close();
            this.out.close();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
