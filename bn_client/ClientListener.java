package TP2.bn_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientListener extends Thread {
    BufferedReader in;
    AtomicBoolean keep;

    /**
     * Constructeur du thread d'écoute
     * @param in BufferedReader pour lire ce qui est envoyé depuis le serveur
     */
    public ClientListener(BufferedReader in) {
        this.in = in;
        this.keep = new AtomicBoolean(true); // booléen permettant de stopper le thread si un message spécial est reçu
    }

    @Override
    public void run() {
        while(this.keep.get()){
            try{
                String msg = in.readLine();
                if(msg.equals("display")){
                    int gridsize = Integer.parseInt(in.readLine());
                    String type = in.readLine();
                    String[] xcoordsString = in.readLine().split(",");
                    String[] ycoordsString = in.readLine().split(",");
                    if(xcoordsString != null && !xcoordsString.equals("") && ycoordsString != null && !ycoordsString.equals("")){
                        int[] xcoords = Arrays.asList(xcoordsString).stream().mapToInt(Integer::parseInt).toArray();
                        int[] ycoords = Arrays.asList(ycoordsString).stream().mapToInt(Integer::parseInt).toArray();
                        display(xcoords,ycoords, type,gridsize);
                    }
                }
                else if(msg.equals("exit")){
                    in.close(); // fermeture du thread
                    return;
                }
                else{
                    System.out.println(msg);
                }
            } catch(IOException e) { e.printStackTrace(); }
        }
    }

    public void display(int[] xcoords, int[] ycoords, String type, int gridSize) {
        String[][] grid = new String[gridSize][gridSize];

        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                grid[i][j] = "~";
            }
        }

        for(int x = 0; x < xcoords.length; x++){
            if(xcoords[x] != -1 && ycoords[x] != -1)
                grid[xcoords[x]][ycoords[x]] = type;
        }

        String s = "";
        for(int i = 0; i < gridSize; i++){
            for(int j = 0; j < gridSize; j++){
                s += "|" + grid[j][i];
            }
            s += "|\n";
        }
        System.out.println(s);
    }

    /**
     * Méthode permettant de stopper la boucle while du thread
     */
    public void close(){ this.keep.set(false); }
}
