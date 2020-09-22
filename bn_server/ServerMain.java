package TP2.bn_server;

import java.io.IOException;

public class ServerMain {

    /**
     * Main permettant de démarrer le serveur
     * @param args Arguments du main
     */
    public static void main(String[] args){
        Server srv = null;
        try {
            srv = new Server(1234);
        } catch(IOException e) { e.printStackTrace(); }
        System.out.println("Server starting");
        srv.execute(); // lancement du serveur
    }
}
