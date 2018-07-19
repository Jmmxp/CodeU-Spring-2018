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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;

import static codeu.model.data.Conversation.*;

public class ConversationTest {

  @Test
  public void testCreate() {
    UUID id = UUID.randomUUID();
    UUID owner = UUID.randomUUID();
    String title = "Test_Title";
    Instant creation = Instant.now();
    List<String> users = new ArrayList<>();
    users.add("Test_User");
    ConversationType conversationType = ConversationType.NORMAL;

    Conversation conversation = new Conversation(id, owner, title, creation, users, conversationType);

    Assert.assertEquals(id, conversation.getId());
    Assert.assertEquals(owner, conversation.getOwnerId());
    Assert.assertEquals(title, conversation.getTitle());
    Assert.assertEquals(creation, conversation.getCreationTime());
    Assert.assertEquals(users, conversation.getUsers());
    Assert.assertEquals(conversation.getNumUsers(), 1);
    Assert.assertEquals(conversationType, conversation.getConversationType());
  }

  @Test
  public void testIsNormalConversation() {
    UUID id = UUID.randomUUID();
    UUID owner = UUID.randomUUID();
    String title = "Test_Title";
    Instant creation = Instant.now();

    Conversation conversation = new Conversation(id, owner, title, creation);

    Assert.assertEquals(conversation.isNormalConversation(), true);
  }

  @Test
  public void testIsNotNormalConversation() {
    UUID id = UUID.randomUUID();
    UUID owner = UUID.randomUUID();
    String title = "Test_Title";
    Instant creation = Instant.now();
    List<User> users = new ArrayList<>();
    users.add(new User(UUID.randomUUID(), "Test_Name", "Test_Hash", Instant.now()));
    users.add(new User(UUID.randomUUID(), "Test_Name2", "Test_Hash2", Instant.now()));

    Conversation conversation = new Conversation(id, owner, title, creation, users, ConversationType.DIRECT);

    Assert.assertEquals(conversation.isNormalConversation(), false);
  }

  @Test
  public void testUserInConversation() {
    UUID id = UUID.randomUUID();
    UUID owner = UUID.randomUUID();
    String title = "Test_Title";
    Instant creation = Instant.now();
    List<User> users = new ArrayList<>();
    users.add(new User(UUID.randomUUID(), "Test_Name", "Test_Hash", Instant.now()));

    Conversation conversation = new Conversation(id, owner, title, creation, users, ConversationType.GROUP);

    Assert.assertEquals(conversation.isUserInConversation("Test_Name"), true);
    Assert.assertEquals(conversation.isUserInConversation("Test_Name2"), false);
  }

}
