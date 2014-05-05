package sample;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Thib'
 * Date: 30/10/13
 * Time: 23:26
 * To change this template use File | Settings | File Templates.
 */
public class Champ {
    private String type;
    private String nom;
    private String label;
    private static ArrayList<Champ> listValeur = new ArrayList<Champ>();   //Contient la liste des objets Champ

    public Champ() {                      //Constructeur
        listValeur = new ArrayList<Champ>();
    }

    public Champ(String nom) {  // Constructeur
        this.nom = nom;
        this.type = "";
    }
                //GETTER/SETTER
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public static ArrayList<Champ> getValeurTable() {
        return listValeur;
    }
    public static void ajoutList(Champ c)  {
        listValeur.add(c);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public static Champ rechercherParNom(String nom){  // Rechercher une valeur par son nom (colonne)
        Champ c = null;
        boolean trouve = false;
        int i = 0;

        while((trouve == false)&&(i < listValeur.size())){
            c = listValeur.get(i);
            if (c.getNom() == nom){
                trouve = true;
            }
            i++;
        }
        if (trouve){
            return c;
        }else{
            return null;
        }
    }
    public static void nettoyage(){
        listValeur.clear();
    }
}
