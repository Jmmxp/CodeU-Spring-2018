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

package codeu.model.store.persistence;

import static codeu.model.data.Conversation.ConversationType;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.Profile;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentDataStoreException;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class handles all interactions with Google App Engine's Datastore service. On startup it
 * sets the state of the applications's data objects from the current contents of its Datastore. It
 * also performs writes of new of modified objects back to the Datastore.
 */
public class PersistentDataStore {

  // Handle to Google AppEngine's Datastore service.
  private DatastoreService datastore;

  /**
   * Constructs a new PersistentDataStore and sets up its state to begin loading objects from the
   * Datastore service.
   */
  public PersistentDataStore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /**
   * Loads all User objects from the Datastore service and returns them in a List.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<User> loadUsers() throws PersistentDataStoreException {

    List<User> users = new ArrayList<>();

    // Retrieve all users from the datastore.
    Query query = new Query("chat-users");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        String userName = (String) entity.getProperty("username");
        String passwordHash = (String) entity.getProperty("password_hash");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        User user = new User(uuid, userName, passwordHash, creationTime);
        users.add(user);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return users;
  }

  /**
   * Loads all Conversation objects from the Datastore service and returns them in a List, sorted in
   * ascending order by creation time.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Conversation> loadConversations() throws PersistentDataStoreException {

    List<Conversation> conversations = new ArrayList<>();

    // Retrieve all conversations from the datastore.
    Query query = new Query("chat-conversations").addSort("creation_time", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID ownerUuid = UUID.fromString((String) entity.getProperty("owner_uuid"));
        String title = (String) entity.getProperty("title");
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        ConversationType type = ConversationType.valueOf((String) entity.getProperty("type"));

        Conversation conversation;
        if (type == ConversationType.NORMAL) {
            conversation = new Conversation(uuid, ownerUuid, title, creationTime);
        } else {
            List<String> users = (List<String>) entity.getProperty("users");
            conversation = new Conversation(uuid, ownerUuid, title, creationTime, users, type);
        }
        conversations.add(conversation);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return conversations;
  }

  /**
   * Loads all Message objects from the Datastore service and returns them in a List, sorted in
   * ascending order by creation time.
   *
   * @throws PersistentDataStoreException if an error was detected during the load from the
   *     Datastore service
   */
  public List<Message> loadMessages() throws PersistentDataStoreException {

    List<Message> messages = new ArrayList<>();

    // Retrieve all messages from the datastore.
    Query query = new Query("chat-messages").addSort("creation_time", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        UUID uuid = UUID.fromString((String) entity.getProperty("uuid"));
        UUID conversationUuid = UUID.fromString((String) entity.getProperty("conv_uuid"));
        UUID authorUuid = UUID.fromString((String) entity.getProperty("author_uuid"));
        Instant creationTime = Instant.parse((String) entity.getProperty("creation_time"));
        String content = (String) entity.getProperty("content");
        Message message = new Message(uuid, conversationUuid, authorUuid, content, creationTime);
        messages.add(message);
      } catch (Exception e) {
        // In a production environment, errors should be very rare. Errors which may
        // occur include network errors, Datastore service errors, authorization errors,
        // database entity definition mismatches, or service mismatches.
        throw new PersistentDataStoreException(e);
      }
    }

    return messages;
  }
  
  /**
	 * Loads all Profile objects from the Datastore service and returns them in a hash table 
	 * 
	 * @throws PersistentDataStoreException 
	 * 				if an error is detected during the load from the Datastore service
	 */
	public HashMap<String, String> loadProfiles() throws PersistentDataStoreException {
		HashMap<String, String> profiles = new HashMap<String, String>();
		Query query = new Query("user-profiles");
		PreparedQuery results = datastore.prepare(query);
		try {
			for (Entity entity : results.asIterable()) {
				String profileOwner = (String) entity.getProperty("owner_name");
				String text = (String) entity.getProperty("text");
				profiles.put(profileOwner, text);
			}
		} catch (Exception e) {
			throw new PersistentDataStoreException(e);
		}

		return profiles;
	}

  /** Write a User object to the Datastore service. */
  public void writeThrough(User user) {
    Entity userEntity = new Entity("chat-users", user.getId().toString());
    userEntity.setProperty("uuid", user.getId().toString());
    userEntity.setProperty("username", user.getName());
    userEntity.setProperty("password_hash", user.getPasswordHash());
    userEntity.setProperty("creation_time", user.getCreationTime().toString());
    datastore.put(userEntity);
  }

  /** Write a Message object to the Datastore service. */
  public void writeThrough(Message message) {
    Entity messageEntity = new Entity("chat-messages", message.getId().toString());
    messageEntity.setProperty("uuid", message.getId().toString());
    messageEntity.setProperty("conv_uuid", message.getConversationId().toString());
    messageEntity.setProperty("author_uuid", message.getAuthorId().toString());
    messageEntity.setProperty("content", message.getContent());
    messageEntity.setProperty("creation_time", message.getCreationTime().toString());
    datastore.put(messageEntity);
  }

  /** Write a Conversation object to the Datastore service. */
  public void writeThrough(Conversation conversation) {
    Entity conversationEntity = new Entity("chat-conversations", conversation.getId().toString());
    conversationEntity.setProperty("uuid", conversation.getId().toString());
    conversationEntity.setProperty("owner_uuid", conversation.getOwnerId().toString());
    conversationEntity.setProperty("title", conversation.getTitle());
    conversationEntity.setProperty("creation_time", conversation.getCreationTime().toString());
    conversationEntity.setProperty("users", conversation.getUsers());
    conversationEntity.setProperty("type", conversation.getConversationType().name());
    datastore.put(conversationEntity);
  }

  public void updateConversation(Conversation conversation) {
      Key conversationKey = KeyFactory.createKey("chat-conversations", conversation.getId().toString());
      Entity conversationEntity;

      try {
          conversationEntity = datastore.get(conversationKey);
      } catch (EntityNotFoundException e) {
          e.printStackTrace();
          return;
      }

      // this line prevents ChatAddUserServletTest from NPE during doPostAddUser test
//      if (conversationEntity == null) return;

      conversationEntity.setProperty("users", conversation.getUsers());
      datastore.put(conversationEntity);
  }

  /** Write a Profile object to the Datastore service. */
	public void writeThrough(Profile profile) {
		Query query = new Query("user-profiles");
		PreparedQuery results = datastore.prepare(query);
		for (Entity entity : results.asIterable()) {
			if (profile.getProfileOwner().equals(entity.getProperty("owner_name"))) {
				entity.removeProperty("text");
				entity.setProperty("text", profile.getProfileText());

				datastore.put(entity);
				return;
				//return new Profile(uuid, profileOwner, text);
			}
		}
		Entity profileEntity = new Entity("user-profiles", profile.getId().toString());
		profileEntity.setProperty("uuid", profile.getId().toString());
		profileEntity.setProperty("owner_name", profile.getProfileOwner().toString());
		profileEntity.setProperty("text", profile.getProfileText());
		datastore.put(profileEntity);
	}
  
  public void deleteAllUsers(List<User> users) {
    for (User user : users) {
      Key userKey = KeyFactory.createKey("chat-users", user.getId().toString());
      datastore.delete(userKey);
    }
  }

  public void deleteAllMessages(List<Message> messages) {
    for (Message message : messages) {
      Key messageKey = KeyFactory.createKey("chat-messages", message.getId().toString());
      datastore.delete(messageKey);
    }
  }

  public void deleteAllConversations(List<Conversation> conversations) {
    for (Conversation conversation : conversations) {
      Key conversationKey = KeyFactory.createKey("chat-conversations", conversation.getId().toString());
      datastore.delete(conversationKey);
    }
  }

}
