package sample;

import javafx.beans.value.ObservableStringValue;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.String;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: Thib'
 * Date: 31/10/13
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class formulaire implements Initializable {
    @FXML TextArea textarea;
    @FXML Button bt_enregistrer;
    @FXML Label messageLabel;
    private String form;
    private String method;
    private ObservableList<String> form1 = FXCollections.observableArrayList();
    private static ArrayList<Champ> listValeur = new ArrayList<Champ>();


    public void initialize(URL url, ResourceBundle resourceBundle){

        bt_enregistrer.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public  void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();    //On ouvre une form contenant la demande de chemin et le nom du fichier voulu
                fileChooser.setTitle("Enregistrer sous");  // Son titre
                fileChooser.getExtensionFilters().addAll(           // Ses extensions
                        new FileChooser.ExtensionFilter("Fichier HTML (*.html)", "*.html")
                );

                File file = fileChooser.showSaveDialog(null);

                if (file != null) {

                    try {
                        // Crée le fichier
                        if (!file.getPath().endsWith(".%")){
                            FileWriter fstream = new FileWriter(file.getPath()+".html");
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
    public void gForm(){         // On recupere le text du formulaire
        listValeur = Champ.getValeurTable();                // On recupere la liste d'objet
        form = "<form method='"+method+"' action='action.php'>"+System.getProperty("line.separator");

        for(Champ c  : formulaire.listValeur){            // On parcours la liste en mettant le type voulu avec des if
            if (!c.getType().equals("")) {
                if (c.getType().equals("liste")){
                    form += "<label>"+ c.getLabel() + "</label><select id=\'"+c.getLabel()+"\'>";
                    ArrayList<String> donnee = ChampValeur.voirDonnee(c.getNom());
                    for(String d  : donnee){
                        form += "<option value=\'"+d+"\'>"+d+"</option>";
                    }
                    form += "</select></br>"+System.getProperty("line.separator");
                } if (c.getType().equals("textarea")){
                    form += "<label>" +c.getLabel() + "</label><textarea name='"+c.getNom()+"' value=''> </textarea></br>"+System.getProperty("line.separator");
                } if ((c.getType().equals("text")) || (c.getType().equals("password"))){
                    form  += "<label>" +c.getLabel() + "</label><input name='"+c.getNom()+"' value='' type='"+c.getType()+"'/></br>"+System.getProperty("line.separator");
                } if ((c.getType().equals("radio"))){
                    form += "<label>" + c.getLabel() + "</label>";
                    ArrayList<String> donnee = ChampValeur.voirDonnee(c.getNom());
                    for(String d  : donnee){
                        form += "<input type='radio' name=\'"+c.getNom() +"\' value=\'"+d+"\'/>"+d;
                    }
                    form += "</br>";
                } if ((c.getType().equals("checkbox"))){
                    form += "<label>" + c.getLabel() + "</label>";
                    ArrayList<String> donnee = ChampValeur.voirDonnee(c.getNom());
                    for(String d  : donnee){
                        form += "<input type='checkbox' name=\'"+c.getNom() +"\' value=\'"+d+"\'/>"+d;
                    }
                    form += "</br>";
                }
            }
        }
        form += "<input name='Valider' type='submit'> <input name='Annuler' type='reset'></form>";
        System.out.println(form);
        textarea.setText(form); // On met le formulaire dans la TextArea
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
