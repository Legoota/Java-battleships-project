package TP2.bn_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class ServerCommunicationHandler extends Thread {
    /*
    BufferedReader[] ins = new BufferedReader[2];
    Socket[] joueurs = new Socket[2];
    PrintWriter[] outs = new PrintWriter[2]; //2 clients d'ecoute max
    Grid[] games = new Grid[2];*/
    BufferedReader[] ins;
    Socket[] joueurs;
    PrintWriter[] outs;
    Grid[] games = new Grid[2];

    /**
     * Constructeur cas solo
     * @param j1 Socket joueur 1
     * @param inJ1 BufferedReader joueur 1
     * @param outJ1 PrintWriter joueur 1
     * @throws IOException Exception I/O
     */
    public ServerCommunicationHandler(Socket j1, BufferedReader inJ1, PrintWriter outJ1) throws IOException {
        this.joueurs = new Socket[1];
        this.ins = new BufferedReader[1];
        this.outs = new PrintWriter[1];
        this.joueurs[0] = j1;
        this.ins[0] = inJ1;
        this.outs[0] = outJ1;
        this.games[0] = new Grid(10);
        this.games[1] = new Grid(10);
    }

    /**
     * Constructeur cas multijoueur
     * @param j1 Socket joueur 1
     * @param inJ1 BufferedReader joueur 1
     * @param outJ1 PrintWriter joueur 1
     * @param j2 Socket joueur 2
     * @throws IOException Exception I/O
     */
    public ServerCommunicationHandler(Socket j1, BufferedReader inJ1, PrintWriter outJ1, Socket j2) throws IOException {
        this.joueurs = new Socket[2];
        this.ins = new BufferedReader[2];
        this.outs = new PrintWriter[2];
        this.joueurs[0] = j1;
        this.joueurs[1] = j2;
        this.ins[0] = inJ1;
        this.ins[1] = new BufferedReader(new InputStreamReader(this.joueurs[1].getInputStream()));
        this.outs[0] = outJ1;
        this.outs[1] = new PrintWriter(this.joueurs[1].getOutputStream(), true);
        this.games[0] = new Grid(10);
        this.games[1] = new Grid(10);
    }

    /**
     * Methode qui envoie un message aux 2 joueurs de la partie
     * @param msg Le message a envoyer
     */
    private void sendMessageAll(String msg) {
        for(int all = 0; all < this.joueurs.length; all++) outs[all].println(msg);
    }

    /**
     * Envoie au client les informations concernant la grille a afficher
     * @param client L'id du client
     * @param type True: grille personnelle, False: grille des tirs
     */
    private void sendDisplayInfo(int client, boolean type) {
        outs[client].println("display"); // affichage grille (coté client)
        if(type){
            outs[client].println("o");
            outs[client].println(this.games[client].getxCoordsString());
            outs[client].println(this.games[client].getyCoordsString());
        }
        else{
            int client_hit = client == 0 ? 1 : 0;
            outs[client].println("x");
            outs[client].println(this.games[client_hit].getxCoordsHitsString());
            outs[client].println(this.games[client_hit].getyCoordsHitsString());
        }

    }

    /**
     * Methode permettant de créer une nouvelle partie et de positionner les navires des joueurs
     */
    public void setBoats() {
        this.games[0] = new Grid(10); // reset des grilles au cas ou on recommence une partie
        this.games[1] = new Grid(10);
        for(int joueur = 0; joueur < this.joueurs.length; joueur++){ // chaque joueur a son tour positionne ses bateaux
            if(joueur == 0) outs[1].println("En attente  du placement des bateaux du premier joueur...");
            outs[joueur].println("Positionnement des bateaux!");
            for(int i = 0; i < this.games[joueur].getFlotte().length; i++){ // parcours des bateaux a positionner
                outs[joueur].println("Positionnez le bateau de longueur " + this.games[joueur].getFlotte()[i].getLength() + ",\n" +
                        "avec les coordonnées x et y de départ (valeur entre 0 et 9 comprise), ainsi que la direction (h: haut, b: bas, g: gauche, d: droite):");
                try {
                    boolean valid;
                    do { // tant que le bateau n'est pas positionnable, on recommence
                        String[] coords = ins[joueur].readLine().split(" ");
                        if(coords.length != 3 || coords[0] == null || coords[1] == null)
                            valid = false; // entrée de l'utilisateur incorrecte
                        else // on admet que l'utilisateur entre correctement 2 int puis un string
                            valid = this.games[joueur].boatBuilder(this.games[joueur].getFlotte()[i],Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), coords[2].toLowerCase());
                        if(!valid) outs[joueur].println("Coordonnées invalides, recommencez:");
                    }
                    while (!valid);
                } catch (IOException e) { e.printStackTrace(); }
                this.games[joueur].updateGrille(); // mise a jour de la grille
                outs[joueur].println("Votre bateau a correctement été placé! Voici votre grille actuelle:");
                //outs[joueur].println(this.games[joueur].toString()); // affichage des bateaux (coté serveur)
                sendDisplayInfo(joueur,true);
            }

            outs[joueur].println("Le placement des bateaux a été effectué avec succes, voici votre grille:");
            sendDisplayInfo(joueur,true);
            if(joueur == 0) outs[joueur].println("En attente du placement des bateaux du second joueur...");
        }
        sendMessageAll("La partie va commencer!\n");
    }

    /**
     * Méthode qui gere une partie de bataille navale: chacun son tour tire a l'aide de coordonnées x et y
     */
    public void game(){
        sendMessageAll("Pile ou face pour connaitre le premier joueur...");
        Random rd = new Random();
        boolean pf = rd.nextBoolean(); // choix du premier joueur, puis gestion du joueur pour chaque tour
        sendMessageAll(pf ? "Pile: le joueur 1 commence" : "Face: le joueur 2 commence");
        int coupsJ1 = 0; // nombre de coups joués pour J1
        int coupsJ2 = 0; // nombre de coups joués pour J2
        while(!this.games[0].isFinished() && !this.games[1].isFinished()){ // tant qu'aucune grille n'est terminée, on continue les tours
            outs[pf ? 0 : 1].println("\nChoisissez les coordonnées de votre tir: x et y séparées d'un espace:");
            outs[!pf ? 0 : 1].println("\nVotre adversaire prépare son tir...");
            try {
                String[] coords; // coordonnées du tir
                do coords = ins[pf ? 0 : 1].readLine().split(" ");
                while(coords.length != 2 || coords[0] == null || coords[1] == null); // gestion d'une mauvaise entrée clavier
                // on admet ici que l'utilisateur n'entre pas un autre charatère qu'un int
                int tir = this.games[!pf ? 0 : 1].setTir(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])); // !pf indique qu'on tire bien sur la grille adverse
                String resultatTir = "";
                if(tir == -1) resultatTir = "Plouf !";
                else if(tir == 0) resultatTir = "Touché !";
                else if(tir == 1) resultatTir = "Coulé !";
                outs[pf ? 0 : 1].println("Feu ! vous avez tiré aux coordonnées x:" + coords[0] + " y:" + coords[1] +
                        "\n" + resultatTir + "\nVoici votre grille de tirs:");
                sendDisplayInfo(pf ? 0 : 1,false);
                //outs[!pf ? 0 : 1].println("Votre adversaire a tiré aux coordonnées x:" + coords[0] + " y:" + coords[1] +
                //        "\n" + resultatTir + "\nVoici votre grille actualisée: \n" + this.games[!pf ? 0 : 1].toString());
                outs[!pf ? 0 : 1].println("Votre adversaire a tiré aux coordonnées x:" + coords[0] + " y:" + coords[1] +
                                "\n" + resultatTir + "\nVoici votre grille actualisée:");
                sendDisplayInfo(!pf ? 0 : 1, true); // affichage grille coté client

            } catch (IOException e) { e.printStackTrace(); }

            sendMessageAll("\nJoueur suivant !\n");
            if (pf) coupsJ1++;
            else coupsJ2++;
            pf = !pf; // changement joueur
        }
        int winner = this.games[0].isFinished() ? 2 : 1; // on recherche le gagnant
        sendMessageAll("La partie est terminée ! \n Le gagnant est le Joueur " + winner + ", en " +
                (winner == 1 ? coupsJ1 : coupsJ2) + " coups !");
    }

    @Override
    public void run() {
        System.out.println("Thread run()");
        boolean keepPlaying = true;
        while(keepPlaying){
            System.out.println("Setting the boats");
            setBoats();
            System.out.println("Playing the game");
            game();
            String[] reponses = new String[2];
            for(int all = 0; all < this.joueurs.length; all++){
                this.outs[all].println("Recommencer une partie ? y / n");
                try {
                    reponses[all] = ins[all].readLine();
                } catch (IOException e) { e.printStackTrace(); }
            }
            keepPlaying = reponses[0].toLowerCase().equals("y") && reponses[1].toLowerCase().equals("y"); // on arrete de jouer si au moins 1 des joueurs ne veut plus (autre réponse que "y")
        }
        sendMessageAll("Merci d'avoir joué, fermeture des connexions... \n Appuyez sur entrée pour fermer");
        close();
    }

    //TODO: add bot
    //TODO: add config (changer taille grille, nombre bateau)

    /**
     * Méthode permettant la fermeture du thread
     */
    private void close(){
        System.out.println("Thread closing");
        sendMessageAll("exit"); // envoi message d'arret aux clients
        try {
            for(int i = 0; i < this.joueurs.length; i++) {
                this.ins[i].close(); // fermeture BufferedReader
                this.joueurs[i].close(); // fermeture Socket
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}