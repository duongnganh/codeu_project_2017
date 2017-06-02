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

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Group;
import codeu.chat.common.Message;
import codeu.chat.common.User;
import codeu.chat.common.IDs;
import codeu.chat.util.Uuid;

public final class BasicControllerTest {

  private Model model;
  private BasicController controller;

  @Before
  public void doBefore() {
    String[] tableNames = {"user", "conversation", "group", "message"};
    model = new Model(IDs.projectId, IDs.instanceId, tableNames);
    controller = new Controller(Uuid.NULL, model);
  }

  @Test
  public void testAddUser() {

    final User user = controller.newUser("user1", "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    model.remove(user);
  }

  @Test
  public void testDeleteUser() {

    final User user = controller.newUser("user2", "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final User delUser = controller.deleteUser(user.name);

    assertFalse(
      "Check that user name entered is valid",
      delUser == null);
  }

  @Test
  public void testAddConversation() {

    final User user = controller.newUser("user3", "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final Group group = controller.newGroup("group3", user.id);

    assertFalse(
        "Check that group has a valid reference",
        group == null);

    final Conversation conversation = controller.newConversation(
        "conversation3",
        user.id, group.id);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);

    model.remove(user);
    model.remove(group);
    model.remove(conversation);
  }

  @Test
  public void testDeleteConversation() {

    final User user = controller.newUser("user4", "nickname", "password");

    assertFalse(
      "Check that user has a valid reference",
      user == null);

    final Group group = controller.newGroup("group4", user.id);

    assertFalse(
      "Check that group has a valid reference",
      group == null);

    final Conversation conversation = controller.newConversation(
      "conversation4",
      user.id, group.id);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);

    final Conversation delConversation = controller.deleteConversation(conversation.title);

    assertFalse(
        "Check that conversation ID is valid",
        delConversation == null);

    model.remove(user);
    model.remove(group);
  }

  @Test
  public void testAddGroup() {

    final User user = controller.newUser("user5", "nickname", "password");

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    final Group group = controller.newGroup(
        "group5",
        user.id);

    assertFalse(
        "Check that group has a valid reference",
        group == null);

    model.remove(user);
    model.remove(group);
  }

  @Test
  public void testAddMessage() {

    final User user = controller.newUser("user6", "nickname", "password");
    final Group group = controller.newGroup("group6", user.id);

    assertFalse(
        "Check that user has a valid reference",
        user == null);

    assertFalse(
        "Check that group has a valid reference",
        group == null);

    final Conversation conversation = controller.newConversation(
        "conversation6",
        user.id, group.id);

    assertFalse(
        "Check that conversation has a valid reference",
        conversation == null);

    final Message message = controller.newMessage(
        user.id,
        conversation.id,
        "Hello World Basic");

    assertFalse(
        "Check that the message has a valid reference",
        message == null);

    model.remove(user);
    model.remove(group);
    model.remove(conversation);
    model.remove(message);
  }
}
