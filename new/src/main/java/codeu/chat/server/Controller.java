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

package codeu.chat.server;

import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Group;
import codeu.chat.common.Message;
import codeu.chat.common.RandomUuidGenerator;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

//For securing passwords
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class Controller implements RawController, BasicController {

  private final static Logger.Log LOG = Logger.newLog(Controller.class);

  private final Model model;
  private final Uuid.Generator uuidGenerator;
  private final byte[] salt;


  public Controller(Uuid serverId, Model model) {
    this.model = model;
    this.uuidGenerator = new RandomUuidGenerator(serverId, System.currentTimeMillis());
    //TODO: fix it
    String mySalt = "My super secret salt";
    this.salt = mySalt.getBytes();
  }

  // Override Basic Controller

  @Override
  public Message newMessage(Uuid author, Uuid conversation, Uuid group, String body) {
    return newMessage(createId(), author, conversation, group, body, Time.now());
  }

  @Override
  public User newUser(String name, String nickname, String pass) {
    User user = newUser(createId(), name, Time.now(), nickname, pass);
    return user;
  }

  @Override
  public User deleteUser(String name) {
    return deleteUser(name, Time.now());
  }

  @Override
  public Conversation newConversation(String title, Uuid owner, Uuid group) {
    return newConversation(createId(), title, owner, group, Time.now());
  }

  @Override
  public Conversation deleteConversation(String title) {
    return deleteConversation(title, Time.now());
  }

  @Override
  public Group newGroup(String title, Uuid owner) {
    return newGroup(createId(), title, owner, Time.now());
  }

  // Override Raw Controller

  @Override
  public Message newMessage(Uuid id, Uuid author, Uuid conversation, Uuid group, String body, Time creationTime) {

    final User foundUser = model.userById().first(author);
    final Conversation foundConversation = model.conversationById().first(conversation);

    Message message = null;

    if (foundUser != null && foundConversation != null && isIdFree(id)) {

      message = new Message(id, Uuid.NULL, Uuid.NULL, creationTime, author, conversation, group, body);
      model.add(message);
      LOG.info("Message added: %s", message.id);

      // Find and update the previous "last" message so that it's "next" value
      // will point to the new message.

      if (Uuid.equals(foundConversation.lastMessage, Uuid.NULL)) {

        // The conversation has no messages in it, that's why the last message is NULL (the first
        // message should be NULL too. Since there is no last message, then it is not possible
        // to update the last message's "next" value.

      } else {
        final Message lastMessage = model.messageById().first(foundConversation.lastMessage);
        lastMessage.next = message.id;
      }

      // If the first message points to NULL it means that the conversation was empty and that
      // the first message should be set to the new message. Otherwise the message should
      // not change.

      foundConversation.firstMessage =
          Uuid.equals(foundConversation.firstMessage, Uuid.NULL) ?
          message.id :
          foundConversation.firstMessage;

      // Update the conversation to point to the new last message as it has changed.

      foundConversation.lastMessage = message.id;

      if (!foundConversation.users.contains(foundUser.id)) {
        foundConversation.users.add(foundUser.id);
      }
    }

    return message;
  }

  public User newUser(Uuid id, String name, Time creationTime){
    return newUser(id, name, creationTime, "", "");
  }
  @Override
  public User newUser(Uuid id, String name, Time creationTime, String nickname, String pass) {

    User user = null;

    if (isIdFree(id)) {
      user = new User(id, name, creationTime, nickname);
      //Generated hashed password
      String generatedPassword = getSecurePassword(pass, salt);
      user.setPassword(generatedPassword);
      model.add(user);

      LOG.info(
          "newUser success (user.id=%s user.name=%s user.nickname=%s user.time=%s)",
          id,
          name,
          nickname,
          creationTime);
    }
    return user;
  }

  @Override
  public User deleteUser(String name, Time deletionTime) {
    final User user = model.userByText().first(name);
    if (user != null) {

      model.remove(user);

      LOG.info(
          "removeUser success (user.id=%s user.name=%s user.time=%s)",
          user.id,
          user.name,
          user.creation);

    } else {

      LOG.info(
          "deleteUser failed - name not found (user.id=%s)", name);
    }

    return user;
  }

  @Override
  public Conversation newConversation(Uuid id, String title, Uuid owner, Uuid group, Time creationTime) {
    final User foundOwner = model.userById().first(owner);
    Conversation conversation = null;

    if (foundOwner != null && isIdFree(id)) {
      conversation = new Conversation(id, owner, group, creationTime, title);
      model.add(conversation);

      LOG.info("Conversation added: " + conversation.id);
    }

    return conversation;
  }

  @Override
  public Conversation deleteConversation(String title, Time deletionTime) {
    final Conversation conversation = model.conversationByText().first(title);
    if (conversation != null) {
      model.remove(conversation);

      LOG.info(
          "removeConversation success (conversation.id=%s conversation.owner=%s conversation.title=%s)",
          conversation.id,
          conversation.owner,
          conversation.title);
    } else {

      LOG.info(
          "deleteConversation failed - title not found (user.id=%s)",
          title);
    }

    return conversation;
  }

  @Override
  public Group newGroup(Uuid id, String title, Uuid owner, Time creationTime) {

    final User foundOwner = model.userById().first(owner);

    Group group = null;

    if (foundOwner != null && isIdFree(id)) {
      group = new Group(id, owner, creationTime, title);
      model.add(group);

      LOG.info("Group added: " + group.id);
    }

    return group;
  }

  //=====================================================================

  public User ifCorrectPassword(Uuid id, String pass){
    User user = model.userById().first(id);
    
    if (user == null) return null;

    String regeneratedPassword = getSecurePassword(pass, salt);
    if (user.password.equals(regeneratedPassword))
      return user;

    return null;
  }

  public User setNickname(Uuid id, String nickname){
    if (model.userById().first(id) == null) return null;
    model.userById().first(id).setNickname(nickname);
    return model.userById().first(id);
  }

  private Uuid createId() {

    Uuid candidate;

    for (candidate = uuidGenerator.make();
         isIdInUse(candidate);
         candidate = uuidGenerator.make()) {

     // Assuming that "randomUuid" is actually well implemented, this
     // loop should never be needed, but just incase make sure that the
     // Uuid is not actually in use before returning it.

    }

    return candidate;
  }

  private boolean isIdInUse(Uuid id) {
    return model.messageById().first(id) != null ||
           model.conversationById().first(id) != null ||
           model.groupById().first(id) != null ||
           model.userById().first(id) != null;
  }

  private boolean isIdFree(Uuid id) { return !isIdInUse(id); }

  private static String getSecurePassword(String passwordToHash, byte[] salt)
  {
    String generatedPassword = null;
    try {
      // Create MessageDigest instance for MD5
      MessageDigest md = MessageDigest.getInstance("MD5");
      //Add password bytes to digest
      md.update(salt);
      //Get the hash's bytes 
      byte[] bytes = md.digest(passwordToHash.getBytes());
      //This bytes[] has bytes in decimal format;
      //Convert it to hexadecimal format
      StringBuilder sb = new StringBuilder();
      for(int i=0; i< bytes.length ;i++)
      {
          sb.append(Integer.toString((bytes[i] & 0xfff) + 0x1000, 32).substring(1));
      }
      //Get complete hashed password in hex format
      generatedPassword = sb.toString();
    } 
    catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return generatedPassword;
  }
}
