package TP2.bn_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
                System.out.println("J1 Connecté, demande 1 joueur ou 2 joueurs...");
                BufferedReader inJ1 = new BufferedReader(new InputStreamReader(client1Socket.getInputStream()));
                PrintWriter outJ1 = new PrintWriter(client1Socket.getOutputStream(), true);
                outJ1.println("Voulez vous jouer seul contre l'ordinateur, ou en multijoueur ?\nRépondez par 1 pour seul, 2 pour multijoueur:");
                String reponse = "";
                do reponse = inJ1.readLine();
                while (!(reponse.equals("1") || reponse.equals("2")));
                int nbJoueurs = Integer.parseInt(reponse);
                outJ1.println("Lancement du jeu " + nbJoueurs + (nbJoueurs == 1 ? " joueur..." : " joueurs, attente connexion 2e joueur..."));
                if(nbJoueurs == 1) {
                    System.out.println("1J, lancement");
                    return;
                }
                else {
                    System.out.println("2J, attente 2e joueur");
                    Socket client2Socket = this.homeSocket.accept(); // acceptation second client
                    System.out.println("J2 Connecté");
                    ServerCommunicationHandler sc = new ServerCommunicationHandler(client1Socket, inJ1, outJ1, client2Socket);
                    Thread t = new Thread(sc);
                    System.out.println("Starting game thread");
                    t.start(); // démarrage d'un thread pour gérer la partie entre J1 et J2
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
