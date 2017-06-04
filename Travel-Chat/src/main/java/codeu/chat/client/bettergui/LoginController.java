// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.chat.client.bettergui;

import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import codeu.chat.client.ClientContext;
import codeu.chat.common.User;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;


import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;


public final class LoginController implements Initializable {

    private final static Logger.Log LOG = Logger.newLog(LoginController.class);

    ClientContext clientContext;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOG.info("initializing");
    }

    public void setClientContext(ClientContext clientContext) {
        LOG.info("setting client context");
        this.clientContext = clientContext;
    }

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton login;

    @FXML
    private JFXButton register;

    @FXML
    private JFXTextField username;

    @FXML
    void onLoginButtonClick(ActionEvent event) throws Exception {

        String username = this.pass.getText();
        String password = this.username.getText();

        Boolean status = clientContext.user.signInUser(username, password);

        if (status) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/codeu/chat/client/bettergui/MainUI.fxml"));

            Parent window2 = loader.load();

            MainUIController mainController = loader.getController();

            mainController.setClientContext(clientContext);

            Stage stage = (Stage) anchorPane.getScene().getWindow();

            stage.setHeight(650);
            stage.setWidth(800);

            stage.getScene().setRoot(window2);

        } else {

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("The credentials provided is not registered!");

            alert.showAndWait();
        }

    }

    @FXML
    void onRegisterButtonClick(ActionEvent event) throws Exception {
        LOG.info("Register button gets called");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/codeu/chat/client/bettergui/RegistrationUI.fxml"));

        Parent window2 = loader.load();

        RegistrationController registrationController = loader.getController();

        registrationController.setClientContext(clientContext);

        Stage stage = (Stage) anchorPane.getScene().getWindow();

        stage.setHeight(475);
        stage.setWidth(427);
        stage.getScene().setRoot(window2);

    }

}
