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

//import java.awt.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.util.Logger;

// Chat - top-level client application - Java Better GUI (using JavaFX)
public final class ChatBetterGui extends Application {

  private final static Logger.Log LOG = Logger.newLog(ChatBetterGui.class);

  //private JFrame mainFrame;


 /* private final ClientContext clientContext;

  // Constructor - sets up the Chat Application
  public ChatBetterGui(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
    launch();
  }
*/
  // Run the GUI client
  /*public void run() {

    try {

        launch();
      //initialize();
      //mainFrame.setVisible(true);
      //primaryStage.setVisible(true);

    } catch (Exception ex) {
      System.out.println("ERROR: Exception in ChatSimpleGui.run. Check log for details.");
      LOG.error(ex, "Exception in ChatSimpleGui.run");
      System.exit(1);
    }
}*/


  @Override
      public void start(Stage primaryStage) {
          primaryStage.setTitle("Hello World!");
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
          primaryStage.setScene(new Scene(root, 300, 250));
          primaryStage.show();
      }

//private void initialize() {}
  // Initialize the GUI
 /* private void initialize() {

    // Outermost frame.
    // NOTE: may have tweak size, or place in scrollable panel.
    //mainFrame = new JFrame("Chat");

    primaryStage = new Stage();
    primaryStage.setTitle("Hello World!");
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
    primaryStage.setScene(new Scene(root, 300, 250));
    primaryStage.show();
  }
*/

}
