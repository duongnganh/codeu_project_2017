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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.net.URL;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    private final static Logger.Log LOG = Logger.newLog(MainUIController.class);

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
    private JFXListView<String> conversationList;

    @FXML
    private JFXButton sendButton;


    public void setClientContext(ClientContext clientContext) {
        LOG.info("setting client context");
        this.clientContext = clientContext;
    }

    @FXML
    void sendMessage(ActionEvent event) {
        String message = this.messageView.getText();

        if (message != null) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            listView.getItems().add( sdf.format(cal.getTime()) + ": " + message);

            /*clientContext.message.addMessage(
            clientContext.user.getCurrent().id,
                    clientContext.conversation.getCurrentId(),
                    clientContext.group.getCurrent().id,
                    messageText);*/

        }
    }

    @Override
    public void initialize (URL url, ResourceBundle rb) {

        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList (
            "+ Best Dessert Places", "+ Night Life", "+ Museum & Exhibitions");
        conversationList.setItems(items);
        conversationList.setMinWidth(190);

        /*
        *

         for (final Message m : clientContext.message.getConversationContents(conversation)) {
            // Display author name if available.  Otherwise display the author UUID.
            final String authorName = clientContext.user.getName(m.author);

            final String displayString = String.format("%s: [%s]: %s",
                ((authorName == null) ? m.author : authorName), m.creation, m.content);

                messageListModel.addElement(displayString);
            }

        *
        * */

        //clientContext.conversation.startConversation(s, clientContext.user.getCurrent().id, clientContext.group.getCurrent().id);

        /*

            final int index = objectList.getSelectedIndex();
            final String data = objectList.getSelectedValue();
            final ConversationSummary cs = ConversationPanel.this.lookupByTitle(data, index);

            clientContext.conversation.setCurrent(cs);

            messagePanel.update(cs);


            *******


            private ConversationSummary lookupByTitle(String title, int index) {

            int localIndex = 0;
                for (final ConversationSummary cs : clientContext.conversation.getConversationSummaries()) {
                    if ((localIndex >= index) && cs.title.equals(title)) {
                        return cs;
                    }
                    localIndex++;
                }
            return null;
            }

        * */

    }

    @FXML
    void onBackClick(ActionEvent event) throws Exception {

        Parent window1 = FXMLLoader.load(getClass().getResource("/codeu/chat/client/bettergui/LoginUI.fxml"));

        Stage mainStage = (Stage) borderPane.getScene().getWindow();

        mainStage.setHeight(400);
        mainStage.setWidth(427);

        //logout

        clientContext.user.signOutUser();


        mainStage.getScene().setRoot(window1);

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