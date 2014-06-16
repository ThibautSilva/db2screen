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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ObjetPhp implements Initializable {
    @FXML TextArea textarea;
    @FXML Button bt_enregistrer;
    @FXML Label messageLabel;
    private static String tableselected;
    private static String bdselected;
    private String form;
    private static ArrayList<Champ> listValeur = new ArrayList<Champ>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        bt_enregistrer.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public  void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();    //On ouvre une form contenant la demande de chemin et le nom du fichier voulu
                fileChooser.setTitle("Enregistrer sous");  // Son titre
                fileChooser.getExtensionFilters().addAll(           // Ses extensions
                        new FileChooser.ExtensionFilter("Fichier PHP (*.php)", "*.php")
                );

                File file = fileChooser.showSaveDialog(null);

                if (file != null) {

                    try {
                        // Crée le fichier
                        if (!file.getPath().endsWith(".%")){
                            FileWriter fstream = new FileWriter(file.getPath()+".php");
                            BufferedWriter out = new BufferedWriter(fstream);
                            out.write(textarea.getText());

                            //Fermer la form
                            out.close();
                            messageLabel.setText("Fichier enregistré !");     // Afficher fichier enregistré
                            messageLabel.setTextFill(Color.rgb(42, 171, 23));
                        }

                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        });
    }

    public void setTableSelected (String selected){       //Set de la table selectionnée
        this.tableselected = selected;
    }
    public void setBdSelected (String selected){      //  Set de la base selectionnée
        this.bdselected = selected;
    }
    public void getValeurTable(){     //On recupere les champs de la table selectionné
        try {
            Connexion.connexion();
            Connection con = Connexion.getCon();    //recupere la connexion
            DatabaseMetaData dmd = Base.con.getMetaData();
            ResultSet columns = dmd.getColumns(null, null, tableselected, null);  //On recupere les meta de la table selectionné
            int i = 0;
            Champ.nettoyage();
            while (columns.next()) {
                Champ c = new Champ(columns.getString("COLUMN_NAME"));                     // On range dans un objet Champ le nom
                Champ.ajoutList(c);                                            //On met chaque objet dans la liste ValeurTable
                i++;
            }
            gObjet();

        }catch (SQLException E) {
        }
    }

    public void gObjet(){
        listValeur = Champ.getValeurTable();
        form = "<?php \n class "+tableselected+" { \n";
        for(Champ c  : listValeur) {
            form += "private $_"+c.getNom()+";\n";
        }
        form += "function __construct(){}\n";
        for(Champ c  : listValeur) {
            form += "public function set"+c.getNom()+"($"+c.getNom()+"){\n$this->_"+c.getNom()+" = $"+c.getNom()+";\n}\n";
            form += "public function get"+c.getNom()+"(){\n return $this->_"+c.getNom()+";\n}\n";
        }
        form += "public function inserer(){\n $sql = \"INSERT INTO "+tableselected+" values ('\"";
        int i = 0;
        for(Champ c  : listValeur) {
            if(i!=0){
                form += ",'\"";
            }
            i++;
            form += ".$this->_"+c.getNom()+".\"'";
        }
        form += ");\";\n$requete = mysql_query($sql);\n}\n";
        form += "public function modifier(){\n $sql = \"UPDATE "+tableselected+" SET ";
        int e = 0;
        for(Champ c  : listValeur) {
            if(e!=0){
                form += " , ";
            }
            e++;
            form += c.getNom()+" = $this->_"+c.getNom();
        }
        form += " WHERE "+ listValeur.get(0).getNom()+" = "+"$this->_"+listValeur.get(0).getNom()+";\";\n$requete = mysql_query($sql);\n}\n";
        form += "public function supprimer(){\n $sql = \"DELETE FROM "+tableselected+" WHERE "+ listValeur.get(0).getNom()+" = "+"$this->_"+listValeur.get(0).getNom();
        form += ";\";\n$requete = mysql_query($sql);\n}\n";
        form += "}";
        textarea.setText(form); // On met le formulaire dans la TextArea
    }
}
