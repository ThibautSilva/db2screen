package sample;

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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: pc thomas
 * Date: 30/10/13
 * Time: 18:11
 * To change this template use File | Settings | File Templates.
 */
public class Connexion implements Initializable {
    @FXML private static TextField loginField;
    @FXML private static TextField mdpField;
    @FXML private static TextField urlField;
    @FXML private static Label messageLabel;
    @FXML private Button bt_valider;
    public static String bd = "";
    public static Connection con;
    public static boolean connexionReussie = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){ //
        bt_valider.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public  void handle(ActionEvent e) {         //Quand on clique sur le bouton On ferme connexion et on ouvre base.fxml
                Connexion.connexion();
                if (connexionReussie == false) {     //Si la connexion n'a pas reussie on affiche un message d'erreur
                    loginField.clear();
                    mdpField.clear();
                    urlField.clear();
                    messageLabel.setText("Mot de passe ou Adresse incorrect");
                    messageLabel.setTextFill(Color.rgb(210, 39, 30));
                }  else {

                    URL location = getClass().getResource("base.fxml");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(location);
                    fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

                    Parent root = null;
                    try {
                        root = (Parent) fxmlLoader.load(location.openStream());
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    Base c =fxmlLoader.getController();
                    c.bdlist = Base.listeTable();       // On liste les noms de la base de données dans bdlist
                    c.setLesBd(c.bdlist);  //  On récupere la liste bdlist pour la mettre en observable

                    Stage popUp = new Stage();
                    popUp.initModality(Modality.WINDOW_MODAL);
                    popUp.initOwner(((Node)e.getSource()).getScene().getWindow());
                    popUp.setScene(new Scene(root, 600,400));
                    popUp.show();
                }
            }
        });
    }
    public static Connection getConnection(String login, String mdp, String url, String bd){ // Connexion à la base de données
        System.out.println("-------- MySQL JDBC Connection Testing ------------");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
        }

        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;

        try {
            System.out.println("url = " +url);
            connection = DriverManager
                    .getConnection("jdbc:mysql://"+ url +"/"+ bd, login, mdp);
            if (url != null){
                connexionReussie = true;
            }else {
            connexionReussie = false;
            }

        } catch (SQLException e) {
            System.out.println("Erreur mot de passe");
            e.printStackTrace();
            connexionReussie = false;     // On dit que la connexion n'as pas réussie
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
            return connection;


    }
    public static void connexion () {  //Lancement de la connexion avec les champs renseignés dans connexion.fxml
        con = Connexion.getConnection(loginField.getText(), mdpField.getText(), urlField.getText(), bd);
    }

    public static Connection getCon() { //On recupère la connexion
        return con;
    }

    public static void setBd(String bdnew) {   // On recupère le nom de la BD
        bd = bdnew;
    }
}
