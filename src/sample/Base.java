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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: pc thomas
 * Date: 30/10/13
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
public class Base implements Initializable {
    @FXML ListView list_bd;
    @FXML ListView list_table;
    @FXML Button bt_valid;
    @FXML Button bt_validphp;
    private ObservableList<String> lesTablesObserves = FXCollections.observableArrayList();
    private ObservableList<String> lesBdObserves = FXCollections.observableArrayList();
    public ArrayList<String> tablelist = new ArrayList();
    public ArrayList<String> bdlist = new ArrayList();
    public static Connection con = Connexion.getCon();
    public String bdselected;
    public String tableselected;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        list_bd.setItems(lesBdObserves);     // On passe les listes dans les listView respectif
        list_table.setItems(lesTablesObserves);
        bt_valid.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public  void handle(ActionEvent e) {             // Au Clic du bouton valider on ouvre base.fxml
                URL location = getClass().getResource("Champ.fxml");

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(location);
                fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load(location.openStream());
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                ChampValeur c =fxmlLoader.getController();
                c.setTableSelected(tableselected);   // On passe la table selectionné
                c.setBdSelected(bdselected);         // On passe la base selectionné
                c.getValeurTable();              // On lance la fonction getValeurTable afin de récuperer les valeurs des tables

                Stage popUp = new Stage();
                popUp.initModality(Modality.WINDOW_MODAL);
                popUp.initOwner(((Node)e.getSource()).getScene().getWindow());
                popUp.setScene(new Scene(root, 455,364));
                popUp.show();

            }
        });
        bt_validphp.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public  void handle(ActionEvent e) {             // Au Clic du bouton valider on ouvre base.fxml
                URL location = getClass().getResource("objetphp.fxml");

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(location);
                fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

                Parent root = null;
                try {
                    root = (Parent) fxmlLoader.load(location.openStream());
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                ObjetPhp c =fxmlLoader.getController();
                c.setTableSelected(tableselected);   // On passe la table selectionné
                c.setBdSelected(bdselected);         // On passe la base selectionné
                c.getValeurTable();              // On lance la fonction getValeurTable afin de récuperer les valeurs des tables

                Stage popUp = new Stage();
                popUp.initModality(Modality.WINDOW_MODAL);
                popUp.initOwner(((Node)e.getSource()).getScene().getWindow());
                popUp.setScene(new Scene(root, 455,364));
                popUp.show();

            }
        });
        list_bd.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
                bdselected = (String) newValue;
                System.out.println("OK" + bdselected);       // On recupere la table selectionneé
                tablelist = getTable(bdselected);    // On recupere dans tablelist la liste des tables de la base selectionné
                setLesTables(tablelist);
            }
        });
        list_table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
                tableselected = (String) newValue;                   // On recupere la table selectionnée
                System.out.println("OK" + tableselected);
            }
        });
    }


    public static ArrayList listeTable (){     //On fait un arraylist de type string contenant les base de données
        ArrayList<String> list = new ArrayList<String>();
        try {
            DatabaseMetaData meta = con.getMetaData();
            ResultSet res = meta.getCatalogs();
            while (res.next()) {                         //On ajoute a la liste des bases de données
                list.add(res.getString("TABLE_CAT"));
            }
            res.close();
            con.close();
        } catch (Exception e) {
            System.err.println("ClassNotFoundException: "
                    +e.getMessage());
        }
        return list;
    }
    public static ArrayList getTable(String bd){    //on recupere les tables par rapport au nom de la base
        ArrayList<String> list = new ArrayList();
        Connexion.setBd(bd);   //On envoi la base de donnée dans base
        Connexion.connexion();    // On lance la connexion
        con = Connexion.getCon();    // On recupere la connexion
        try {
            //on récupère les métadonnées à partir de la connexion
            DatabaseMetaData dmd = con.getMetaData();
            //récupération des informations
            ResultSet tables = dmd.getTables(con.getCatalog(),null,"%",null);
            //affichage des noms des tables
            while(tables.next()){
                String tableName = tables.getString("TABLE_NAME" );
                list.add(tableName);
            }
        }catch (Exception e ){
        }
        return list;  // on retourne la liste de nom des tables
    }

    public void setLesBd(ArrayList<String> liste) {  //Met les BD dans une table observes
        this.bdlist = liste;
        for(String bd  : bdlist){
            this.lesBdObserves.add(bd);
        }
    }

    public void setLesTables(ArrayList<String> liste) {    //Met la table list dans une table observes
        this.tablelist = liste;
        this.lesTablesObserves.clear();
        for(String bd  : tablelist){
            this.lesTablesObserves.add(bd);
        }
    }
}

