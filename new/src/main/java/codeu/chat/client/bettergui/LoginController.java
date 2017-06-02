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


public final class LoginController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXPasswordField pass;

    @FXML
    private JFXButton login;

    @FXML
    private JFXTextField username;

    @FXML
    void onLoginButtonClick(ActionEvent event) throws Exception {

        /*clientContext.user.addUser("Testname");
        for (final User u : clientContext.user.getUsers()) {
            System.out.print(u.name);
        }*/

        // Parent window1 = FXMLLoader.load(getClass().getResource("/codeu/chat/client/bettergui/MainUI.fxml"));
        Parent window1 = FXMLLoader.load(getClass().getResource("MainUI.fxml"));

        Stage mainStage = (Stage) anchorPane.getScene().getWindow();

        mainStage.setHeight(650);
        mainStage.setWidth(800);

        mainStage.getScene().setRoot(window1);

    }

}
