package TP2.bn_server;

import java.util.Arrays;
import java.util.Vector;

public class Grid {
    // o : emplacement bateau
    // x : emplacement tir adversaire
    // ~ : emplacement eau
    String[][] grid; // grille navires joueur
    Vector<Integer> xHits; // coordonnées x des tirs du joueur adverse sur la grille du joueur
    Vector<Integer> yHits; // coordonnées y des tirs du joueur adverse sur la grille du joueur
    int gridSize;
    Boat[] flotte;

    public Grid(int tailleGrille){
        this.grid = new String[tailleGrille][tailleGrille];
        this.xHits = new Vector<>();
        this.yHits = new Vector<>();
        this.gridSize = tailleGrille;

        resetGrille();

        this.flotte = new Boat[5]; // taille fixe du nombre de bateau, possibilité de changer pour autre taille de grille
        int[] boats =  {5,4,3,3,2}; // taille des bateaux
        for(int i = 0; i < 5; i++) this.flotte[i] = new Boat(boats[i]);
    }

    /**
     * Reset de la grille actuelle
     */
    private void resetGrille() {
        for(int i = 0; i < this.gridSize; i++){
            for(int j = 0; j < this.gridSize; j++){
                this.grid[i][j] = "~";
            }
        }
    }

    /**
     * Mise a jour de l'affichage de la grille
     */
    public void updateGrille(){
        resetGrille();
        for (Boat boat : this.flotte) {
            for (int j = 0; j < boat.getLength(); j++) {
                if (!boat.getHits()[j] && !(boat.getXcoords()[j] == -1)) // si l'emplacement n'est pas touché, alors on le marque comme emplacement avec bateau
                    this.grid[boat.getXcoords()[j]][boat.getYcoords()[j]] = "o";
            }
        }
    }

    public boolean alreadyShot(int x, int y) {
        for(int i = 0; i < this.xHits.size(); i++)
            if(this.xHits.get(i) == x && this.xHits.get(i) == y) return true;
        return false;
    }

    /**
     * Methode pour obtenir toutes les coordonnées x des bateaux
     * @return Chaine de caractères des coordonnées en x des bateaux, séparées par ,
     */
    public String getxCoordsString() {
        String res = "";
        for(Boat boat: this.flotte){
            res += Arrays.toString(boat.getXcoords()).replaceAll("\\s+","").replaceAll("\\[","").replaceAll("]","");
            res+=",";
        }
        return res.substring(0,res.length()-1);
    }

    /**
     * Methode pour obtenir toutes les coordonnées y des bateaux
     * @return Chaine de caractères des coordonnées en y des bateaux, séparées par ,
     */
    public String getyCoordsString() {
        String res = "";
        for(Boat boat: this.flotte){
            res += Arrays.toString(boat.getYcoords()).replaceAll("\\s+","").replaceAll("\\[","").replaceAll("]","");
            res+=",";
        }
        return res.substring(0,res.length()-1);
    }

    /**
     * Methode permettant de récupérer l'ensemble des coordonnées x des tirs du joueur adverse sur la grille du joueur courant
     * @return Chaine de caractères des coordonnées x, séparées par ,
     */
    public String getxCoordsHitsString() {
        String res = this.xHits.toString().replaceAll("\\s+","").replaceAll("\\[","").replaceAll("]","");
        return res;
    }

    /**
     * Methode permettant de récupérer l'ensemble des coordonnées y des tirs du joueur adverse sur la grille du joueur courant
     * @return Chaine de caractères des coordonnées y, séparées par ,
     */
    public String getyCoordsHitsString() {
        return this.yHits.toString().replaceAll("\\s+","").replaceAll("\\[","").replaceAll("]","");
    }

    /**
     * Methode qui gere le tir adverse
     * @param x Coordonnée x du tir
     * @param y Coordonnée y du tir
     * @return -1 si dans l'eau, 0 si touché, 1 si coulé
     */
    public int setTir(int x, int y){
        if(x > 9 || y > 9 || x < 0 || y < 0) return -1;
        int res = -1; // -1 = dans l'eau
        this.xHits.add(x);
        this.yHits.add(y);
        for (Boat boat : this.flotte)
            if (boat.isHit(x, y)) {
                res = 0; // 0 = touché
                if (boat.isCoule())
                    res = 1; // 1 = coulé
                updateGrille();
            }
        return res;
    }

    public Boat[] getFlotte() { return this.flotte; }

    /**
     * Méthode qui vérifie si cette grille contient encore des bateaux ou non
     * @return True si aucun bateaux, False sinon
     */
    public boolean isFinished() {
        for (Boat boat : this.flotte) {
            if (!boat.isCoule()) return false;
        }
        return true;
    }

    /**
     * Méthode qui gère la présence d'un bateau aux coordonées x y
     * @param x Coordonnée x du bateau b a placer
     * @param y Coordonnée y du bateau b a placer
     * @param b Bateau a placer
     * @return True si un bateau est déjà présent, False sinon
     */
    public boolean alreadyOccupied(int x, int y, Boat b) {
        for (Boat boat : this.flotte) {
            for (int j = 0; j < boat.getLength(); j++) {
                if (boat.getXcoords()[j] == x && boat.getYcoords()[j] == y) { // si un bateau est deja present, on reset le debut de bateau construit
                    b.setXcoords(new int[]{-1, -1, -1, -1, -1});
                    b.setYcoords(new int[]{-1, -1, -1, -1, -1});
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Méthode qui place un bateau dans la grille
     * @param b Le bateau a placer
     * @param x Coordonnée x du début du bateau
     * @param y Coordonée y du début du bateau
     * @param direction Direction dans laquelle le bateau doit être créé
     * @return True si le bateau est bien placé, False sinon
     */
    public boolean boatBuilder(Boat b, int x, int y, String direction) {
        if(x < 0 || y < 0 || x > 9 || y > 9 || direction == null) return false; // position de depart hors grille
        if(!"hbgd".contains(direction)) return false; // direction non valide
        switch (direction) { // verification si le bateau ne sort pas de la grille
            case "d":
                if(x + b.getLength() > 9) return false; // on verifie que le bateau entre en entier dans la grille
                for(int i = 0; i < b.getLength(); i++) {
                    if(alreadyOccupied(x+i,y,b)) return false; // on verifie que chaque partie du bateau ne chevauche pas un autre bateau
                    b.setXcoord(i,x+i);
                    b.setYcoord(i,y);
                }
                break;
            case "g":
                if(x - b.getLength() < 0) return false;
                for(int i = 0; i < b.getLength(); i++) {
                    if(alreadyOccupied(x-i,y,b)) return false;
                    b.setXcoord(i,x-i);
                    b.setYcoord(i,y);
                }
                break;
            case "h":
                if(y - b.getLength() < 0) return false;
                for(int i = 0; i < b.getLength(); i++) {
                    if(alreadyOccupied(x,y-i,b)) return false;
                    b.setXcoord(i,x);
                    b.setYcoord(i,y-i);
                }
                break;
            case "b":
                if(y + b.getLength() > 9) return false;
                for(int i = 0; i < b.getLength(); i++) {
                    if(alreadyOccupied(x,y+i,b)) return false;
                    b.setXcoord(i,x);
                    b.setYcoord(i,y+i);
                }
                break;
        }
        return true;
    }

    public String toString(){
        String s = "";
        for(int i = 0; i < this.gridSize; i++){
            for(int j = 0; j < this.gridSize; j++){
                s += "|" + this.grid[j][i];
            }
            s += "|\n";
        }
        return s;
    }
}
