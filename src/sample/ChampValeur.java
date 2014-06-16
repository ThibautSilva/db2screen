package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Thib'
 * Date: 30/10/13
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */

public class ChampValeur implements Initializable {
    @FXML ListView valeursdestables;
    @FXML Button bt_valid;
    @FXML Button générer;
    @FXML ComboBox typebox;
    @FXML TextField labelfield;
    @FXML RadioButton getbutton;
    @FXML RadioButton postbutton;
    private ObservableList<String> ValeursDesTablesObserves = FXCollections.observableArrayList();
    private static String tableselected;
    private static String bdselected;
    private String colonne;
    public ArrayList<Champ> ValeurTable = new ArrayList<Champ>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        valeursdestables.setItems(ValeursDesTablesObserves);       // On met la list observes des valeurs des tables dans la listView
        typebox.getSelectionModel().select(0);
        valeursdestables.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
                labelfield.clear();     // On efface la valeur du labelfield
                typebox.getSelectionModel().select(0);    //On met la valeur 0 au comboBox par défault Text
                colonne = (String) newValue;   // On recupere dans colonne la valeur selectionnée
                System.out.println("OK" + colonne);
            }
        });
        bt_valid.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Champ c = Champ.rechercherParNom(colonne);  // Lors du clique sur le bouton valider on recherche l'objet Champ ayant pour nom la variable colonne
                typeLabel(c);    // On fait appel à la fonction typeLabel avec 'c' comme parametre
            }
        });
        générer.setOnAction(new EventHandler<ActionEvent>() {   // Quand le bouton generer est cliquer
            @Override
            public void handle(ActionEvent e) {
                URL location = getClass().getResource("formulaire.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(location);
                fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load(location.openStream());
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                formulaire c =fxmlLoader.getController();
                if (getbutton.isSelected()){  //Si le Radiobutton get est selectionné alors on envoie la methode get dans le formulaire
                    c.setMethod(getbutton.getText());
                    System.out.println(getbutton.getText());
                }
                if (postbutton.isSelected()){       //De même pour Post
                    c.setMethod(postbutton.getText());
                    System.out.println(postbutton.getText());
                }
                c.gForm();          //On lance la methode gForm de formulaire

                Stage popUp = new Stage();
                popUp.initModality(Modality.WINDOW_MODAL);
                popUp.initOwner(((Node)e.getSource()).getScene().getWindow());
                popUp.setScene(new Scene(root, 497,308));
                popUp.show();

            }
        });
    }

    public void setTableSelected (String selected){       //Set de la table selectionnée
        this.tableselected = selected;
    }
    public void setBdSelected (String selected){      //  Set de la base selectionnée
        this.bdselected = selected;
    }
    public void setLesValeursDesTables() {     // On met la liste ValeurTable récuperer de getValeurTable de champ dans une liste observes
        this.ValeurTable.clear();
        ValeursDesTablesObserves.clear();
        this.ValeurTable = Champ.getValeurTable();
        for(Champ c  : this.ValeurTable){
            this.ValeursDesTablesObserves.add(c.getNom());
        }
    }
    public void getValeurTable(){     //On recupere les champs de la table selectionné
        try {
            Connexion.connexion();
            Connection con = Connexion.getCon();    //recupere la connexion
            DatabaseMetaData dmd = Base.con.getMetaData();
            ResultSet columns = dmd.getColumns(null, null, tableselected, null);  //On recupere les meta de la table selectionné
            int i = 0;
            Champ.nettoyage();
            while (columns.next())
            {
                Champ c = new Champ(columns.getString("COLUMN_NAME"));                     // On range dans un objet Champ le nom
                Champ.ajoutList(c);                                            //On met chaque objet dans la liste ValeurTable
                System.out.println(columns.getString("COLUMN_NAME"));
                i++;
            }
            setLesValeursDesTables();      // On range la list des noms dans une list observable

        }catch (SQLException E) {
        }
    }
    public static ArrayList<String> voirDonnee(String nom){  // On recupère la liste des valeurs d'une proprieté d'une table
        ArrayList<String> donnee= new ArrayList<String>();
        Connexion.setBd(bdselected);
        Connexion.connexion();
        Connection con = Connexion.getCon();
        String query =  "SELECT DISTINCT " + nom +" FROM " + tableselected;  //Sélectionne les noms venant de la table sélectionner
        ResultSet results;
        try {
            Statement stmt = con.createStatement();

            results = stmt.executeQuery(query);
            while(results.next()){
                donnee.add(results.getString(nom));
            }
        }
        catch(Exception e){}
        return donnee;
    }
    public void typeLabel (Champ c){            // On recupere l'objet selectionnée et on lui met le label et son type
        c.setType(typebox.getValue().toString());
        c.setLabel(labelfield.getText());
        System.out.println(labelfield.getText()+": "+ typebox.getValue());
    }

}


