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

import jdk.internal.jline.internal.Nullable;

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
  public final List<String> users;
  public final ConversationType conversationType;

  public enum ConversationType {
    NORMAL, DIRECT, GROUP
  }

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
    this.conversationType = ConversationType.NORMAL;
  }

  /**
   * Constructs a new Direct Message or Group Conversation.
   *
   * @param id the ID of this Conversation
   * @param owner the ID of the User who created this Conversation
   * @param title the title of this Conversation
   * @param creation the creation time of this Conversation
   * @param users the Users or user names that will be able to access and chat in this conversation
   * @param conversationType the type of Conversation
   */
  public Conversation(UUID id, UUID owner, String title, Instant creation, List<?> users,
                      ConversationType conversationType) {
    this.id = id;
    this.owner = owner;
    this.creation = creation;
    this.title = title;

    // Check if list of Users or Strings was given
    if (users.get(0) instanceof User) {
      List<String> usernames = new ArrayList<>();
      for (User user : (List<User>) users) {
        usernames.add(user.getName());
      }
      this.users = usernames;
    } else if (users.get(0) instanceof String) {
      this.users = (List<String>) users;
    } else {
      throw new IllegalArgumentException("Users list should be of type User or String!");
    }

    this.conversationType = conversationType;
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

  /** Returns the list of usernames that can access and chat in this Conversation */
  public List<String> getUsers() {
    return users;
  }

  /** Returns the number of users that can access and chat in this Conversation*/
  public int getNumUsers() {
    return users.size();
  }

  public ConversationType getConversationType() {
    return conversationType;
  }

  public boolean addUser(User user) {
    if (user == null) {
      return false;
    }
    users.add(user.getName());
    return true;
  }

  /** Adds a user to the user List by using their username
   * @param username Username of the user to add
   * @return boolean whether or not the user was found and added into the List */
  public boolean addUser(String username) {
    if (username == null) {
      return false;
    }
    users.add(username);
    return true;
  }

  /** Returns whether or not this Conversation is a normal conversation */
  public boolean isNormalConversation() {
    return conversationType == ConversationType.NORMAL;
  }

  /** Returns whether or not this Conversation is a direct message conversation */
  public boolean isDirectConversation() {
    return conversationType == ConversationType.DIRECT;
  }

  /** Returns whether or not this Conversation is a group conversation */
  public boolean isGroupConversation() {
    return conversationType == ConversationType.GROUP;
  }

  /** Returns whether or not the user is the user List for this Conversation */
  public boolean isUserInConversation(String username) {
    if (username == null || isNormalConversation()) {
      return false;
    }

    for (String username2 : users) {
      if (username.equals(username2)) {
        return true;
      }
    }

    return false;

  }

  /**
   * @param currentUser The username of the currently logged in user
   * @return String the title of the direct conversation by checking the username besides currentUser
   * if the conversation is not direct then return the default title
   * */
  @Nullable
  public String getDirectConversationTitle(String currentUser) {
    if (!isDirectConversation()) {
      return title;
    }

    String userOne = users.get(0);
    String userTwo = users.get(1);
    if (currentUser.equals(userOne)) {
      return userTwo;
    } else if (currentUser.equals(userTwo)) {
      return userOne;
    }

    return title;
  }

}
