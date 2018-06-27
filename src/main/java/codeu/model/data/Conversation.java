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

package codeu.model.data;

import codeu.model.store.basic.UserStore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class representing a conversation, which can be thought of as a chat room. Conversations are
 * created by a User and contain Messages.
 */
public class Conversation {
  public final UUID id;
  public final UUID owner;
  public final Instant creation;
  public final String title;
  public final List<User> users;


  /**
   * Constructs a new Conversation.
   *
   * @param id the ID of this Conversation
   * @param owner the ID of the User who created this Conversation
   * @param title the title of this Conversation
   * @param creation the creation time of this Conversation
   */
  public Conversation(UUID id, UUID owner, String title, Instant creation) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;
    this.users = new ArrayList<>();
  }

  /** Returns the ID of this Conversation. */
  public UUID getId() {
    return id;
  }

  /** Returns the ID of the User who created this Conversation. */
  public UUID getOwnerId() {
    return owner;
  }

  /** Returns the title of this Conversation. */
  public String getTitle() {
    return title;
  }

  /** Returns the creation time of this Conversation. */
  public Instant getCreationTime() {
    return creation;
  }

  /** Returns the list of users that can access and chat in this Conversation */
  public List<User> getUsers() {
    return users;
  }

  /** Adds a user to the user List by using their username
   * @param username Username of the user to add
   * @return whether or not the user was found and added into the List */
  public boolean addUser(String username) {
    if (username == null) return false;

    User user = UserStore.getInstance().getUser(username);
    if (user != null) {
      users.add(user);
      System.out.println("added user" + user);
      return true;
    }

    return false;
  }

  /** Returns whether or not this Conversation is a normal conversation Direct Message */
  public boolean isNormalConversation() {
    // The convention will be that all normal conversations don't have anyone in their user List.
    return users.size() == 0;
  }

  /** Returns whether or not the user is the user List for this Conversation */
  public boolean isUserInConversation(String username) {
    if (username == null || isNormalConversation()) {
      return false;
    }

    for (User user : users) {
      if (username.equals(user.getName())) {
        return true;
      }
    }

    return false;

  }

}
