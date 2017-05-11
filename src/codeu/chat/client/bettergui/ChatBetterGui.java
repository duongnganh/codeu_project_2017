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
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

//import com.jfoenix.*;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

// Chat - top-level client application - Java Better GUI (using JavaFX)
public final class ChatBetterGui extends Application {

    private final static Logger.Log LOG = Logger.newLog(ChatBetterGui.class);


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/codeu/chat/client/bettergui/Untitled.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        /*primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));*/
        primaryStage.show();
    }

}
