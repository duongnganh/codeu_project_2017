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

package codeu.chat.common;

import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

// RAW CONTROLLER
//
// A controller that grants a large amount of control over how data is inserted
// into the model. If there is a conflict in data, the call will be rejected and
// a null value returned.
public interface RawController {

  // NEW MESSAGE
  //
  // Add a new message to the model with a specific id. If the id is already
  // in use, the call will fail and null will be returned.
  Message newMessage(Uuid id, Uuid author, Uuid conversation, Uuid group, String body, Time creationTime);

  // NEW USER
  //
  // Add a new user to the model with a specific id. If the id is already in
  // use, the call will fail and null will be returned.
  User newUser(Uuid id, String name, Time creationTime, String nickname, String pass);

  // DELETE USER

  // Deletes a user from the model with a specific name. If the id doesn't exist,
  // the call with fail and the method will return False.
  User deleteUser(String name, Time deletionTime);

  // NEW CONVERSATION
  //
  // Add a new conversation to the model with a specific if. If the id is
  // already in use, the call will fail and null will be returned.
  Conversation newConversation(Uuid id, String title, Uuid owner, Uuid group, Time creationTime);

  // DELETE CONVERSATION

  // Deletes a conversation from the model with a specific title. If the id doesn't exist,
  // the call with fail and the method will return False.
  Conversation deleteConversation(String title, Time deletionTime);

  // NEW GROUP
  //
  // Add a new group to the model with a specific if. If the id is
  // already in use, the call will fail and null will be returned.
  Group newGroup(Uuid id, String title, Uuid owner, Time creationTime);

}
