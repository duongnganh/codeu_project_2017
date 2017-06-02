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

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

import codeu.chat.common.Conversation;
import codeu.chat.common.Group;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class RawControllerTest {

  private Model model;
  private RawController controller;

  private Uuid userId;
  private Uuid conversationId;
  private Uuid groupId;
  private Uuid messageId;

  @Before
  public void doBefore() {
    String[] tableNames = {"user", "conversation", "group", "message"};
    model = new Model("codeuproject12345", "codeuinstance12345", tableNames);
    controller = new Controller(Uuid.NULL, model);

    userId = new Uuid(1);
    conversationId = new Uuid(2);
    messageId = new Uuid(3);
    groupId = new Uuid(4);
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser(userId, "user1", Time.now(), "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    model.refresh();
  }

  @Test
  public void testDeleteUser() {

    final User user = controller.newUser(userId, "user2", Time.now(), "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final User delUser = controller.deleteUser("user2", Time.now());

    assertFalse(
        "Check that user ID has a valid reference",
        delUser == null);
    assertTrue(
        "Check that the user has the correct username",
        Uuid.equals(delUser.id, userId));

    model.refresh();
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser(userId, "user3", Time.now(), "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final Group group = controller.newGroup(groupId, "group3", user.id, Time.now());

    assertFalse(
        "Check that group has a valid reference",
        group == null);
    assertTrue(
        "Check that the group has the correct id",
        Uuid.equals(group.id, groupId));

    final Conversation conversation = controller.newConversation(
        conversationId,
        "conversation3",
        user.id,
        group.id,
        Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(conversation.id, conversationId));

    model.refresh();
  }

  @Test
  public void testDeleteConversation() {

    final User user = controller.newUser(userId, "user4", Time.now(), "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final Group group = controller.newGroup(groupId, "group4", user.id, Time.now());

    assertFalse(
        "Check that group has a valid reference",
        group == null);
    assertTrue(
        "Check that the group has the correct id",
        Uuid.equals(group.id, groupId));

    final Conversation conversation = controller.newConversation(
        conversationId,
        "conversation4",
        user.id,
        group.id,
        Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(conversation.id, conversationId));

    final Conversation delConversation = controller.deleteConversation("conversation4", Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        delConversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(delConversation.id, conversationId));

    model.refresh();

  }

  @Test
  public void testAddGroup() {

    final User user = controller.newUser(userId, "user5", Time.now(), "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final Group group = controller.newGroup(
        groupId,
        "group5",
        user.id,
        Time.now());

    assertFalse(
        "Check that group has a valid reference",
        group == null);
    assertTrue(
        "Check that the group has the correct id",
        Uuid.equals(group.id, groupId));

    model.refresh();
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser(userId, "user6", Time.now(), "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);
    assertTrue(
        "Check that the user has the correct id",
        Uuid.equals(user.id, userId));

    final Group group = controller.newGroup(groupId, "group6", user.id, Time.now());

    assertFalse(
    "Check that group has a valid reference",
        group == null);
    assertTrue(
    "Check that the group has the correct id",
        Uuid.equals(group.id, groupId));

    final Conversation conversation = controller.newConversation(
        conversationId,
        "conversation6",
        user.id,
        group.id,
        Time.now());

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);
    assertTrue(
        "Check that the conversation has the correct id",
        Uuid.equals(conversation.id, conversationId));

    final Message message = controller.newMessage(
        messageId,
        user.id,
        conversation.id,
        "Hello World Raw",
        Time.now());

    assertFalse(
        "Check that the message has a valid reference",
        message == null);
    assertTrue(
        "Check that the message has the correct id",
        Uuid.equals(message.id, messageId));
    model.refresh();
  }
}
