package TP2.bn_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket homeSocket;

    public Server(int port) throws IOException {
        this.homeSocket = new ServerSocket(port);
    }

    /**
     * Boucle d'exécution du serveur
     */
    public void execute(){
        while(true){
            try {
                Socket client1Socket = this.homeSocket.accept(); // acceptation premier client
                System.out.println("J1 Connecté, attente J2...");
                Socket client2Socket = this.homeSocket.accept(); // acceptation second client
                System.out.println("J2 Connecté");
                ServerCommunicationHandler sc = new ServerCommunicationHandler(client1Socket, client2Socket);
                Thread t = new Thread(sc);
                System.out.println("Starting game thread");
                t.start(); // démarrage d'un thread pour gérer la partie entre J1 et J2
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
