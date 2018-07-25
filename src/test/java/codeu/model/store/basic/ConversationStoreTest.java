package codeu.model.store.basic;

import codeu.helper.ConversationHelper;
import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.persistence.PersistentStorageAgent;
import java.time.Instant;
import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static codeu.model.data.Conversation.*;

public class ConversationStoreTest {

  private ConversationStore conversationStore;
  private PersistentStorageAgent mockPersistentStorageAgent;

  private final Conversation CONVERSATION_ONE =
      new Conversation(
          UUID.randomUUID(), UUID.randomUUID(), "conversation_one", Instant.ofEpochMilli(1000));

  @Before
  public void setup() {
    mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);
    conversationStore = ConversationStore.getTestInstance(mockPersistentStorageAgent);

    final List<Conversation> conversationList = new ArrayList<>();
    conversationList.add(CONVERSATION_ONE);
    conversationStore.setConversations(conversationList);
  }

  @Test
  public void testGetConversationWithTitle_found() {
    Conversation resultConversation =
        conversationStore.getConversationWithTitle(CONVERSATION_ONE.getTitle());

    assertEquals(CONVERSATION_ONE, resultConversation);
  }

  @Test
  public void testGetConversationWithTitle_notFound() {
    Conversation resultConversation = conversationStore.getConversationWithTitle("unfound_title");

    Assert.assertNull(resultConversation);
  }

  @Test
  public void testIsTitleTaken_true() {
    boolean isTitleTaken = conversationStore.isTitleTaken(CONVERSATION_ONE.getTitle());

    Assert.assertTrue(isTitleTaken);
  }

  @Test
  public void testIsTitleTaken_false() {
    boolean isTitleTaken = conversationStore.isTitleTaken("unfound_title");

    Assert.assertFalse(isTitleTaken);
  }

  @Test
  public void testAddConversation() {
    Conversation inputConversation =
        new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());

    conversationStore.addConversation(inputConversation);
    Conversation resultConversation =
        conversationStore.getConversationWithTitle("test_conversation");

    assertEquals(inputConversation, resultConversation);
    Mockito.verify(mockPersistentStorageAgent).writeThrough(inputConversation);
  }

  @Test
  public void testGetNumConversations() {
    // Note that there is already a conversation added in setup as well
    Conversation conversationOne =
            new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());

    conversationStore.addConversation(conversationOne);

    Assert.assertEquals(conversationStore.getNumConversations(), 2);
  }

  @Test
  public void testDeleteConversations() {
    Conversation conversationOne =
            new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now());
    Conversation conversationTwo =
            new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation2", Instant.now());

    conversationStore.addConversation(conversationOne);
    conversationStore.addConversation(conversationTwo);
    // Note that there is already a conversation added in setup as well
    Assert.assertEquals(conversationStore.getNumConversations(), 3);

    List<Conversation> conversations = conversationStore.getAllConversations();

    conversationStore.deleteAllConversations();

    Mockito.verify(mockPersistentStorageAgent).deleteAllConversations(conversations);
    Assert.assertEquals(conversationStore.getNumConversations(), 0);
  }

  @Test
  public void testGetDirectMessageWithUsers() {
    User userOne = new User(UUID.randomUUID(), "Justin", "testHash", Instant.now());
    User userTwo = new User(UUID.randomUUID(), "Cynthia", "testHash2", Instant.now());
    User userThree = new User(UUID.randomUUID(), "Vasu", "testHash3", Instant.now());
    List<User> users = new ArrayList<>();
    users.add(userOne);
    users.add(userTwo);
    Conversation conversation = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "testTitle",
            Instant.now(), ConversationHelper.getUsernamesFromUsers(users), ConversationType.DIRECT);

    users.add(userThree);
    Conversation conversationTwo = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "testTitle",
            Instant.now(), ConversationHelper.getUsernamesFromUsers(users), ConversationType.DIRECT);

    conversationStore.addConversation(conversation);
    conversationStore.addConversation(conversationTwo);

    Assert.assertEquals(conversationStore.getDirectMessageWithUsers("Justin", "Cynthia"), conversation);
    Assert.assertNull(conversationStore.getDirectMessageWithUsers("Cynthia", "Vasu"));
  }

  @Test
  public void testGetConversationsForUser() {
    List<String> usersOne = new ArrayList<>();
    List<String> usersTwo = new ArrayList<>();

    usersOne.add("Cynthia");
    usersTwo.add("Vasu");

    Conversation conversation = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "testTitle",
            Instant.now(), usersOne, ConversationType.DIRECT);

    Conversation conversationTwo = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "testTitle",
            Instant.now(), usersTwo, ConversationType.DIRECT);

    conversationStore.addConversation(conversation);
    conversationStore.addConversation(conversationTwo);
    Assert.assertEquals(3, conversationStore.getNumConversations());

    Set<Conversation> expectedConversations = new HashSet<>();
    expectedConversations.add(conversation);
    expectedConversations.add(CONVERSATION_ONE);

    Set<Conversation> actualConversations = new HashSet<>
            ((conversationStore.getConversationsForUser("Cynthia")));

    Assert.assertEquals(expectedConversations, actualConversations);
    Assert.assertEquals(2, actualConversations.size());
  }

  private void assertEquals(Conversation expectedConversation, Conversation actualConversation) {
    Assert.assertEquals(expectedConversation.getId(), actualConversation.getId());
    Assert.assertEquals(expectedConversation.getOwnerId(), actualConversation.getOwnerId());
    Assert.assertEquals(expectedConversation.getTitle(), actualConversation.getTitle());
    Assert.assertEquals(
        expectedConversation.getCreationTime(), actualConversation.getCreationTime());
  }
}
