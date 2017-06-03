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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainUIController implements Initializable {

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

    @FXML
    void sendMessage(ActionEvent event) {
        String message = this.messageView.getText();

        if (message != null) {

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            listView.getItems().add( sdf.format(cal.getTime()) + ": " + message);

        }
    }

    @Override
    public void initialize (URL url, ResourceBundle rb) {

        ListView<String> list = new ListView<String>();
        ObservableList<String> items = FXCollections.observableArrayList (
                "+ Around the Eiffel Tower", "+ Best Dessert Places", "+ Night Life", "+ Museum & Exhibitions");
        conversationList.setItems(items);
        conversationList.setMinWidth(190);
    }

    @FXML
    void onBackClick(ActionEvent event) throws Exception {

        Parent window1 = FXMLLoader.load(getClass().getResource("/codeu/chat/client/bettergui/LoginUI.fxml"));

        Stage mainStage = (Stage) borderPane.getScene().getWindow();

        mainStage.setHeight(400);
        mainStage.setWidth(427);


        mainStage.getScene().setRoot(window1);

    }

}