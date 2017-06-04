package codeu.chat.client.bettergui;

import com.jfoenix.controls.*;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import java.net.URL;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//for big table
import com.google.cloud.bigtable.hbase.BigtableConfiguration;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import codeu.chat.common.IDs;

import java.util.ArrayList;

public class MainUIController implements Initializable {

    String city = "NewYork";
    String topic = "Food";

    private final static Logger.Log LOG = Logger.newLog(MainUIController.class);

    private static int counter;

    ClientContext clientContext;

    @FXML
    private JFXButton backButton;

    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXListView<String> listView;

    @FXML
    private JFXTextField messageView;

    @FXML
    private ListView<String> conversationList;

    @FXML
    private JFXButton sendButton;

    @FXML
    private JFXButton parisButton;
    @FXML
    private JFXButton berlinButton;
    @FXML
    private JFXButton newYorkButton;

    @FXML
    void clickParis(ActionEvent event) {
        city = "Paris";
        ArrayList<ArrayList<String>> a = retrieveAllMessages(this.city, this.topic);
        listView.getItems().clear();
        for (ArrayList<String> arr : a) {

            String message = arr.get(1);
            String tsp = arr.get(2);
            String user = arr.get(0);

            String msg = tsp + " - " + user  +": " + message;
            listView.getItems().add(msg);
        }
    }

    @FXML
    void clickBerlin(ActionEvent event) {
        city = "Berlin";
        ArrayList<ArrayList<String>> a = retrieveAllMessages(this.city, this.topic);
        listView.getItems().clear();
        for (ArrayList<String> arr : a) {

            String message = arr.get(1);
            String tsp = arr.get(2);
            String user = arr.get(0);

            String msg = tsp + " - " + user  +": " + message;
            listView.getItems().add(msg);
        }
    }
    @FXML
    void clickNewYork(ActionEvent event) {
        city = "NewYork";
        ArrayList<ArrayList<String>> a = retrieveAllMessages(this.city, this.topic);
        listView.getItems().clear();
        for (ArrayList<String> arr : a) {

            String message = arr.get(1);
            String tsp = arr.get(2);
            String user = arr.get(0);

            String msg = tsp + " - " + user  +": " + message;
            listView.getItems().add(msg);
        }
    }


    public void setClientContext(ClientContext clientContext) {
        LOG.info("setting client context");
        this.clientContext = clientContext;
    }

    @FXML
    void sendMessage(ActionEvent event) {
        String message = this.messageView.getText();

        if (message != null) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d HH:mm:ss");

            String tsp = sdf.format(cal.getTime());

            //String user = clientContext.user.getCurrent().getNickname();

            String user = clientContext.user.getCurrent().getNickname();

            String msg = tsp + " - " + user  +": " + message;

            listView.getItems().add(msg);

            this.messageView.clear();

            counter = (int) (new Date().getTime()/1000);

            insert(this.city, this.topic, user, message, tsp, counter);

        }
    }



    @Override
    public void initialize (URL url, ResourceBundle rb) {

        counter = (int) (new Date().getTime()/1000);
        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList (
            "Food", "Attractions", "Culture");
        conversationList.setItems(items);
        conversationList.setMinWidth(190);

        ArrayList<ArrayList<String>> a = retrieveAllMessages(this.city, this.topic);
        for (ArrayList<String> arr : a) {
            String message = arr.get(1);
            String tsp = arr.get(2);
            String user = arr.get(0);
            String msg = tsp + " - " + user  +": " + message;
            listView.getItems().add(msg);
        }

        conversationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                LOG.info("ListView selection changed from oldValue = "
                        + oldValue + " to newValue = " + newValue);
                topic = newValue;
                ArrayList<ArrayList<String>> a = retrieveAllMessages(city, topic);
                listView.getItems().clear();
                for (ArrayList<String> arr : a) {

                    String message = arr.get(1);
                    String tsp = arr.get(2);
                    String user = arr.get(0);

                    String msg = tsp + " - " + user  +": " + message;
                    listView.getItems().add(msg);
                }
            }
        });


    }

    @FXML
    void onBackClick(ActionEvent event) throws Exception {

        //logout
        clientContext.user.signOutUser();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/codeu/chat/client/bettergui/LoginUI.fxml"));

        Parent window2 = loader.load();

        LoginController loginController = loader.getController();

        loginController.setClientContext(clientContext);

        Stage stage = (Stage) borderPane.getScene().getWindow();

        stage.setHeight(410);
        stage.setWidth(427);
        stage.getScene().setRoot(window2);

    }

    // For big table
    private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("cf1");

    void insert(String city, String conversation, String username, String message, String timestamp, int counter){
        
        String table_name = city+"-"+conversation;

        try (Connection connection = BigtableConfiguration.connect(IDs.projectId, IDs.instanceId)) {
            
            Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(table_name)));

            int rowKey = counter;

            Put put = new Put(Bytes.toBytes(rowKey));
            //Uuid id, String name, Time creation, String nickname
            put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("username"), Bytes.toBytes(username));
            put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("message"), Bytes.toBytes(message));
            put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes("timestamp"), Bytes.toBytes(timestamp));
            table.put(put);

        } catch (IOException e) {
            System.err.println("Exception while running Bigtable: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // return List of rows. each row is an arraylist of 3 Strings: username, message and timestamp
    // change the return if you need so.
    ArrayList<ArrayList<String>> retrieveAllMessages(String city, String conversation){
        
        String table_name = city+"-"+conversation;

        try (Connection connection = BigtableConfiguration.connect(IDs.projectId, IDs.instanceId)) {
            Table table = connection.getTable(TableName.valueOf(Bytes.toBytes(table_name)));
            Scan scan = new Scan();

            System.out.println("Scan for all message for mainUI:");
            ResultScanner scanner = table.getScanner(scan);

            ArrayList<ArrayList<String>> al_rows = new ArrayList<>();
            for (Result row : scanner) {
                String username = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("username")));
                String message = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("message")));
                String timestamp = Bytes.toString(row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes("timestamp")));

                ArrayList<String> al_row = new ArrayList<>();
                al_row.add(username);
                al_row.add(message);
                al_row.add(timestamp);

                al_rows.add(al_row);
            }
            return al_rows;
        } catch (IOException e) {
            System.err.println("Exception while running GetUsers in Model: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}