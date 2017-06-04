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
//

package codeu.chat;

import java.io.IOException;

import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.server.NoOpRelay;
import codeu.chat.server.RemoteRelay;
import codeu.chat.server.Server;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;

import java.io.IOException;

final class ServerMain {

  private static final Logger.Log LOG = Logger.newLog(ServerMain.class);

  public static void main(String[] args) {

    Logger.enableConsoleOutput();

    try {
      Logger.enableFileOutput("bin/chat_server_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    String id_str = System.getProperty("id");
    String secret_str = System.getProperty("secret");
    String port_str = System.getProperty("port");
    String relay_str = System.getProperty("relay");
    final String persistentPath = System.getProperty("persistent_dir");

    final String projectId = System.getProperty("projectId");
    final String instanceId = System.getProperty("instanceId");

    final String userTable = System.getProperty("userTable");
    final String conversationTable = System.getProperty("conversationTable");
    final String groupTable = System.getProperty("groupTable");
    final String messageTable = System.getProperty("messageTable");
    final String[] tableNames = {userTable, conversationTable, groupTable, messageTable};

    Uuid id = null;
    try {
      id = Uuid.parse(id_str);
    } catch (IOException ex) {
      System.out.println("Invalid id - shutting down server");
      System.exit(1);
    }
    final byte[] secret = Secret.parse(secret_str);
    final int myPort = Integer.parseInt(port_str);
    final RemoteAddress relayAddress = relay_str != null ?
       RemoteAddress.parse(relay_str) :
       null;

    try (
      final ConnectionSource serverSource = ServerConnectionSource.forPort(myPort);
      final ConnectionSource relaySource = relayAddress == null ? null : new ClientConnectionSource(relayAddress.host, relayAddress.port)
    ) {

      LOG.info("Starting server...");
      runServer(id, secret, serverSource, relaySource, projectId, instanceId, tableNames);

    } catch (IOException ex) {

      LOG.error(ex, "Failed to establish connections");

    }
  }
  private static void runServer(Uuid id,
                                byte[] secret,
                                ConnectionSource serverSource,
                                ConnectionSource relaySource,
                                String projectId,
                                String instanceId,
                                String[] tableNames) {
    final Relay relay = relaySource == null ?
                        new NoOpRelay() :
                        new RemoteRelay(relaySource);

    final Server server = new Server(id, secret, relay, projectId, instanceId, tableNames);

    LOG.info("Created server.");

    while (true) {

      try {

        LOG.info("Established connection...");
        final Connection connection = serverSource.connect();
        LOG.info("Connection established.");

        server.handleConnection(connection);

      } catch (IOException ex) {
        LOG.error(ex, "Failed to establish connection.");
      }
    }
  }
}
