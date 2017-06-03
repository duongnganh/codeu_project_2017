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
        LOG.info("Paris got clicked");
    }
    @FXML
    void clickBerlin(ActionEvent event) {
        LOG.info("Berlin got clicked");
    }
    @FXML
    void clickNewYork(ActionEvent event) {
        LOG.info("New York got clicked");
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
            "+ Best Dessert Places", "+ Night Life", "+ Museums & Exhibitions");
        conversationList.setItems(items);
        conversationList.setMinWidth(190);

        conversationList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                LOG.info("ListView selection changed from oldValue = "
                        + oldValue + " to newValue = " + newValue);
            }
        });

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

}