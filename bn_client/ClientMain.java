package TP2.bn_client;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain {

    /**
     * Main permettant de démarrer un client de bataille navale
     * @param args Arguments du main
     */
    public static void main(String[] args){
        try{
            Scanner sc = new Scanner(System.in);
            System.out.println("Entrez l'adresse IP du serveur: ");
            String host = sc.nextLine();
            System.out.println("Entrez le port du serveur: ");
            int port = sc.nextInt();
            System.out.println("Connexion au serveur " + host + ":" + port + " ...");

            Client c = new Client(host,port);
            c.execute(); // démarrage du client
        } catch(IOException e) { e.printStackTrace(); }
    }
}
