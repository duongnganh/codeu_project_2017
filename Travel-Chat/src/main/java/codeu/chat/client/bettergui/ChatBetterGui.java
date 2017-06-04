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

import codeu.chat.client.Controller;
import codeu.chat.client.bettergui.ChatBetterGui;
import codeu.chat.client.View;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;

// Chat - top-level client application - Java Better GUI (using JavaFX)
public final class ChatBetterGui extends Application {

    private final static Logger.Log LOG = Logger.newLog(ChatBetterGui.class);

    private ClientContext clientContext;

    Scene s1, s2;

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            Logger.enableFileOutput("bin/chat_better_gui_client_log.log");
        } catch (IOException ex) {
            LOG.error(ex, "Failed to set logger to write to file");
        }

        LOG.info("============================= START OF LOG =============================");

        LOG.info("Starting chat client...");

        // Start up server connection/interface.


        Parameters parameters = getParameters();
        List<String> unnamed = parameters.getUnnamed();

        final RemoteAddress address = RemoteAddress.parse(unnamed.get(0));

        try (
                final ConnectionSource source = new ClientConnectionSource(address.host, address.port)
        ) {
            final Controller controller = new Controller(source);
            final View view = new View(source);

            clientContext = new ClientContext(controller, view);

            LOG.info("Creating client...");
            //runClient(controller, view);

        } catch (Exception ex) {
            System.out.println("ERROR: Exception setting up client. Check log for details.");
            LOG.error(ex, "Exception setting up client.");
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/codeu/chat/client/bettergui/LoginUI.fxml"));

        Parent root = loader.load();

        LoginController loginController = loader.getController();

        loginController.setClientContext(clientContext);

        s1 = new Scene(root);

        primaryStage.setScene(s1);

        primaryStage.show();
    }

}
