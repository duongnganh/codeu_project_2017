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

package codeu.chat.client.commandline;

import java.util.Scanner;

import codeu.chat.client.ClientContext;
import codeu.chat.client.ClientConversation;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.ConversationSummary;
import codeu.chat.common.GroupSummary;
import codeu.chat.util.Logger;
import java.io.Console;
import codeu.chat.util.Uuid;

// Chat - top-level client application.
public final class Chat {

  private final static Logger.Log LOG = Logger.newLog(Chat.class);

  private static final String PROMPT = ">>";

  private final static int PAGE_SIZE = 10;

  private boolean alive = true;

  private final ClientContext clientContext;

  // Constructor - sets up the Chat Application
  public Chat(Controller controller, View view) {
    clientContext = new ClientContext(controller, view);
  }

  // Print help message.
  private static void help() {
    System.out.println("Chat commands:");
    System.out.println("   exit      - exit the program.");
    System.out.println("   help      - this help message.");
    System.out.println("   sign-in <username>  - sign in as user <username>.");
    System.out.println("   sign-out  - sign out current user.");
    System.out.println("   current   - show current user, conversation, message.");
    System.out.println("User commands:");
    System.out.println("   u-add <name> [n-add <nickname>] - add a new user [with nickname]");
    System.out.println("   u-delete <username> - delete an existing user");
    System.out.println("   u-list-all    - list all users known to system.");
    System.out.println("Nickname commands:");
    System.out.println("   n-add <name>  - set a new nickname for the current user");
    System.out.println("Group commands:");
    System.out.println("   g-add <title>    - add a new group.");
    System.out.println("   g-list-all       - list all groups known to system.");
    System.out.println("   g-select <index> - select group from list.");
    System.out.println("Conversation commands:");
    System.out.println("   c-add <title>    - add a new conversation.");
    System.out.println("   c-list-all       - list all conversations known to system.");
    System.out.println("   c-select <index> - select conversation from list.");
    System.out.println("   c-delete <title> - delete a conversation by ID");
    System.out.println("Message commands:");
    System.out.println("   m-add <body>     - add a new message to the current conversation.");
    System.out.println("   m-list-all       - list all messages in the current conversation.");
    System.out.println("   m-next <index>   - index of next message to view.");
    System.out.println("   m-show <count>   - show next <count> messages.");
  }

  // Prompt for new command.
  private void promptForCommand() {
    System.out.print(PROMPT);
    System.out.flush();
  }

  // Parse and execute a single command.
  private void doOneCommand(Scanner lineScanner) {

    final Scanner tokenScanner = new Scanner(lineScanner.nextLine());
    if (!tokenScanner.hasNext()) {
      return;
    }
    final String token = tokenScanner.next();

    if (token.equals("exit")) {

      alive = false;

    } else if (token.equals("help")) {

      help();

    } else if (token.equals("sign-in")) {

      if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: No user name supplied.");
      } else {
        String username = tokenScanner.nextLine().trim();
        Console console = System.console();
        String pass = new String(console.readPassword("Enter your secret password: "));
        signInUser(username, pass);
      }

    } else if (token.equals("sign-out")) {

      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else {
        signOutUser();
      }

    } else if (token.equals("current")) {

      showCurrent();

    } else if (token.equals("u-add")) {

      if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: Username not supplied.");
      } else {
        String name = tokenScanner.nextLine().trim();
        Console console = System.console();
        String pass = new String(console.readPassword("Enter your secret password: "));
        String confirm = new String(console.readPassword("Confirm your password: "));
        addUser(name, pass, confirm);
      }

    } else if (token.equals("u-delete")) {
      if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: Username not supplied.");
      } else {
        String parseId = tokenScanner.nextLine().trim();
        System.out.format("PRINT: Username: %s\n", parseId);
        //Uuid userId = Uuid.fromString(parseId);
        deleteUser(parseId);
      }

    } else if (token.equals("u-list-all")) {

      showAllUsers();

    } else if (token.equals("n-add")) {

      if (!tokenScanner.hasNext()) {
        System.out.println("ERROR: Nickname not supplied.");
      } else {
        addNickname(tokenScanner.nextLine().trim());
      }

    } else if (token.equals("g-add")) {

      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else {
        if (!tokenScanner.hasNext()) {
          System.out.println("ERROR: Group name not supplied.");
        } else {
          final String name = tokenScanner.nextLine().trim();
          clientContext.group.startGroup(name, clientContext.user.getCurrent().id);
        }
      }

    } else if (token.equals("g-list-all")) {
      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else {
        clientContext.group.showAllGroups();
      }

    } else if (token.equals("g-select")) {

      selectGroup(lineScanner);

    } else if (token.equals("c-add")) {

      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else if (!clientContext.group.hasCurrent()) {
        System.out.println("ERROR: No group selected.");
      } else {
        if (!tokenScanner.hasNext()) {
          System.out.println("ERROR: Conversation title not supplied.");
        } else {
          final String title = tokenScanner.nextLine().trim();
          clientContext.conversation.startConversation(title, 
            clientContext.user.getCurrent().id, 
            clientContext.group.getCurrent().id);
        }
      }

    } else if (token.equals("c-delete")) {

      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else {
        if (!tokenScanner.hasNext()) {
          System.out.println("ERROR: Conversation name not supplied.");
        } else {
          final String title = tokenScanner.nextLine().trim();
          clientContext.conversation.deleteConversation(title);
          //Uuid conversationID = Uuid.fromString(title);
          //clientContext.conversation.deleteConversation(conversationID);
        }
      }

    } else if (token.equals("c-list-all")) {
      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else if (!clientContext.group.hasCurrent()) {
        System.out.println("ERROR: No group selected.");
      } else {
        clientContext.conversation.showAllConversations(clientContext.group.getCurrent().id);
      }

    } else if (token.equals("c-select")) {

      selectConversation(lineScanner);

    } else if (token.equals("m-add")) {

      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else if (!clientContext.group.hasCurrent()) {
        System.out.println("ERROR: No group selected.");
      } else if (!clientContext.conversation.hasCurrent()) {
        System.out.println("ERROR: No conversation selected.");
      } else {
        if (!tokenScanner.hasNext()) {
          System.out.println("ERROR: Message body not supplied.");
        } else {
          clientContext.message.addMessage(clientContext.user.getCurrent().id,
              clientContext.conversation.getCurrentId(),
              clientContext.group.getCurrent().id,
              tokenScanner.nextLine().trim());
        }
      }

    } else if (token.equals("m-list-all")) {

      if (!clientContext.user.hasCurrent()) {
        System.out.println("ERROR: Not signed in.");
      } else if (!clientContext.group.hasCurrent()) {
        System.out.println("ERROR: No group selected.");
      } else if (!clientContext.conversation.hasCurrent()) {
        System.out.println("ERROR: No conversation selected.");
      } else {
        clientContext.message.showAllMessages(
          clientContext.group.getCurrent().id, clientContext.conversation.getCurrent().id);
      }

    } else if (token.equals("m-next")) {

      // TODO: Implement m-next command to jump to an index in the message chain.
      if (!clientContext.conversation.hasCurrent()) {
        System.out.println("ERROR: No conversation selected.");
      } else if (!tokenScanner.hasNextInt()) {
        System.out.println("Command requires an integer message index.");
      } else {
        clientContext.message.selectMessage(tokenScanner.nextInt());
      }

    } else if (token.equals("m-show")) {

      // TODO: Implement m-show command to show N messages (currently just show all)
      if (!clientContext.conversation.hasCurrent()) {
        System.out.println("ERROR: No conversation selected.");
      } else {
        final int count = (tokenScanner.hasNextInt()) ? tokenScanner.nextInt() : 1;
        clientContext.message.showMessages(count);
      }

    } else {

      System.out.format("Command not recognized: %s\n", token);
      System.out.format("Command line rejected: %s%s\n", token,
          (tokenScanner.hasNext()) ? tokenScanner.nextLine() : "");
      System.out.println("Type \"help\" for help.");
    }
    tokenScanner.close();
  }

  // Sign in a user.
  private void signInUser(String name, String pass) {
    if (!clientContext.user.signInUser(name, pass)) {
      System.out.println("Error: sign in failed (wrong password?)");
    }
  }

  // Sign out a user.
  private void signOutUser() {
    if (!clientContext.user.signOutUser()) {
      System.out.println("Error: sign out failed (not signed in?)");
    }
  }

  // Helper for showCurrent() - show message info.
  private void showCurrentMessage() {
    if (clientContext.conversation.currentMessageCount() == 0) {
      System.out.println(" -- no messages in conversation --");
    } else {
      System.out.format(" conversation has %d messages.\n",
                        clientContext.conversation.currentMessageCount());
      if (!clientContext.message.hasCurrent()) {
        System.out.println(" -- no current message --");
      } else {
        System.out.println("\nCurrent Message:");
        clientContext.message.showCurrent();
      }
    }
  }

  // Show current user, conversation, message, if any
  private void showCurrent() {
    boolean displayed = false;
    if (clientContext.user.hasCurrent()) {
      System.out.println("User:");
      clientContext.user.showCurrent();
      System.out.println();
      displayed = true;
    }

    if (clientContext.conversation.hasCurrent()) {
      System.out.println("Conversation:");
      clientContext.conversation.showCurrent();

      showCurrentMessage();

      System.out.println();
      displayed = true;
    }

    if (!displayed) {
      System.out.println("No current user or conversation.");
    }
  }

  // Display current user.
  private void showCurrentUser() {
    if (clientContext.user.hasCurrent()) {
      clientContext.user.showCurrent();
    } else {
      System.out.println("No current user.");
    }
  }

  // Display current conversation.
  private void showCurrentConversation() {
    if (clientContext.conversation.hasCurrent()) {
      clientContext.conversation.showCurrent();
    } else {
      System.out.println(" No current conversation.");
    }
  }

  // Add a new user.
  private void addUser(String name, String pass, String confirm) {
    if (!pass.equals(confirm)){
      System.out.println("Password doesn't match");
      return;
    }
    String[] name_nickname = name.split("n-add");
    String username = name_nickname[0].trim();
    String nickname = name_nickname.length == 1 ? "" : name_nickname[1].trim();
    clientContext.user.addUser(username, nickname, pass);
  }

  // Add a new nickname.
  private void addNickname(String name) {
    clientContext.user.addNickname(name);
  }

  // Delete a user
  private void deleteUser(String name) {
    clientContext.user.deleteUser(name);
  }

  // Display all users known to server.
  private void showAllUsers() {
    clientContext.user.showAllUsers();
  }

  public boolean handleCommand(Scanner lineScanner) {

    try {
      promptForCommand();
      doOneCommand(lineScanner);
    } catch (Exception ex) {
      System.out.println("ERROR: Exception during command processing. Check log for details.");
      LOG.error(ex, "Exception during command processing");
    }

    // "alive" may have been set to false while executing a command. Return
    // the result to signal if the user wants to keep going.

    return alive;
  }

  public void selectConversation(Scanner lineScanner) {

    clientContext.conversation.updateAllConversations(false);
    final int selectionSize = clientContext.conversation.conversationsCount();
    System.out.format("Selection contains %d entries.\n", selectionSize);

    final ConversationSummary previous = clientContext.conversation.getCurrent();
    ConversationSummary newCurrent = null;

    if (selectionSize == 0) {
      System.out.println("Nothing to select.");
    } else {
      final ListNavigator<ConversationSummary> navigator =
          new ListNavigator<ConversationSummary>(
              clientContext.conversation.getConversationSummaries(),
              lineScanner, PAGE_SIZE);
      if (navigator.chooseFromList()) {
        newCurrent = navigator.getSelectedChoice();
        clientContext.message.resetCurrent(newCurrent != previous);
        System.out.format("OK. Conversation \"%s\" selected.\n", newCurrent.title);
      } else {
        System.out.println("OK. Current Conversation is unchanged.");
      }
    }
    if (newCurrent != previous) {
      clientContext.conversation.setCurrent(newCurrent);
      clientContext.conversation.updateAllConversations(true);
    }
  }

  public void selectGroup(Scanner lineScanner) {

    clientContext.group.updateAllGroups(false);
    final int selectionSize = clientContext.group.groupsCount();
    System.out.format("Selection contains %d entries.\n", selectionSize);

    final GroupSummary previous = clientContext.group.getCurrent();
    GroupSummary newCurrent = null;

    if (selectionSize == 0) {
      System.out.println("Nothing to select.");
    } else {
      final ListNavigator<GroupSummary> navigator =
          new ListNavigator<GroupSummary>(
              clientContext.group.getGroupSummaries(),
              lineScanner, PAGE_SIZE);
      if (navigator.chooseFromList()) {
        newCurrent = navigator.getSelectedChoice();
        clientContext.message.resetCurrent(newCurrent != previous);
        System.out.format("OK. Group \"%s\" selected.\n", newCurrent.title);
      } else {
        System.out.println("OK. Current Group is unchanged.");
      }
    }
    if (newCurrent != previous) {
      clientContext.group.setCurrent(newCurrent);
      clientContext.group.updateAllGroups(true);
    }
  }
}
