package TP2.bn_server;

public class Boat {

    private int length;
    
    private int[] xcoords, ycoords;
    private boolean[] hits;

    public Boat(int l){
        this.length = l;
        this.xcoords = new int[l];
        this.ycoords = new int[l];
        this.hits = new boolean[l]; // valeur par defaut = false = pas touché
        for(int i = 0; i < l; i++){ // valeur par defaut des bateaux: hors grille
            this.xcoords[i] = -1;
            this.ycoords[i] = -1;
        }
    }

    public int getLength() { return this.length; }

    public int[] getXcoords() { return this.xcoords; }

    public void setXcoords(int[] c) { this.xcoords = c; }

    public void setXcoord(int pos, int val) { this.xcoords[pos] = val; }

    public int[] getYcoords() { return this.ycoords; }

    public void setYcoords(int[] c) { this.ycoords = c; }

    public void setYcoord(int pos, int val) { this.ycoords[pos] = val; }

    public boolean[] getHits() { return this.hits; }

    /**
     * Methode permettant de savoir si un bateau est coulé
     * @return True si le bateau est coulé, False sinon
     */
    public boolean isCoule() {
        for(int i = 0; i < this.length; i++)
            if(!this.hits[i]) return false;
        return true;
    }

    /**
     * Methode permettant de savoir si un bateau est touché par un tir aux coordonnées x y
     * @param x Coordonnée x du tir
     * @param y Coordonnée y du tir
     * @return True si le bateau est touché, False sinon
     */
    public boolean isHit(int x, int y) {
        for(int i = 0; i < this.length; i++) // parcours des coordonnées du bateau
            if(this.xcoords[i] == x && this.ycoords[i] == y){ // si des coordonnées correspondent au tir
                this.xcoords[i] = -1;
                this.ycoords[i] = -1;
                this.hits[i] = true; // on marque le bateau comme touché
                return true;
            }
        return false;
    }
}
