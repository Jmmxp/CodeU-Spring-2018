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

package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentStorageAgent;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static codeu.model.data.Conversation.ConversationType;

public class ChatAddUserServletTest {

    private ChatAddUserServlet chatAddUserServlet;
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;
    private HttpServletResponse mockResponse;
    private ConversationStore mockConversationStore;
    private UserStore mockUserStore;
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Before
    public void setup() {
        chatAddUserServlet = new ChatAddUserServlet();

        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        mockResponse = Mockito.mock(HttpServletResponse.class);

        mockConversationStore = Mockito.mock(ConversationStore.class);
        chatAddUserServlet.setConversationStore(mockConversationStore);

        mockUserStore = Mockito.mock(UserStore.class);
        chatAddUserServlet.setUserStore(mockUserStore);
    }

    @Test
    public void testDoPost_AddInvalidUserToConversation() throws IOException, ServletException {
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/add-user/test_conversation");
        Mockito.when(mockSession.getAttribute("user")).thenReturn("Justin");

        User fakeUser =
                new User(
                        UUID.randomUUID(),
                        "Justin",
                        "testHash",
                        Instant.now());
        Mockito.when(mockUserStore.getUser("Justin")).thenReturn(fakeUser);

        List<User> users = new ArrayList<>();
        users.add(fakeUser);

        Conversation fakeConversation =
                new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now(), users,
                        ConversationType.GROUP);
        Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
                .thenReturn(fakeConversation);

        Mockito.when(mockRequest.getParameter("addNewUser")).thenReturn("notNull");
        Mockito.when(mockRequest.getParameter("newUser")).thenReturn(null);

        chatAddUserServlet.doPost(mockRequest, mockResponse);

        Assert.assertNotNull(mockRequest.getParameter("addNewUser"));
        Mockito.verify(mockUserStore).getUser((String) null);

        Assert.assertEquals(fakeConversation.isUserInConversation("Justin"), true);
        Assert.assertEquals(fakeConversation.getNumUsers(), 1);

        Mockito.verify(mockSession).setAttribute("addNewUserMessage", "Couldn't find that user");
        Mockito.verify(mockResponse)
                .sendRedirect("/chat/" + fakeConversation.getTitle());
    }

    @Test
    public void testDoPost_AddValidUserToConversation() throws IOException, ServletException {
        Mockito.when(mockRequest.getRequestURI()).thenReturn("/chat/add-user/test_conversation");
        Mockito.when(mockSession.getAttribute("user")).thenReturn("fakeUser");

        User fakeUser =
                new User(
                        UUID.randomUUID(),
                        "fakeUser",
                        "testHash",
                        Instant.now());
        Mockito.when(mockUserStore.getUser("fakeUser")).thenReturn(fakeUser);

        List<User> users = new ArrayList<>();
        users.add(fakeUser);

        Conversation fakeConversation =
                new Conversation(UUID.randomUUID(), UUID.randomUUID(), "test_conversation", Instant.now(), users,
                        ConversationType.GROUP);
        Mockito.when(mockConversationStore.getConversationWithTitle("test_conversation"))
                .thenReturn(fakeConversation);

        // Add the conversation to the fake datastore so addUser() can update the conversation later
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        datastoreService.put(new Entity("chat-conversations", fakeConversation.getId().toString()));

        User addedUser =
                new User(
                        UUID.randomUUID(),
                        "addedUser",
                        "testHash",
                        Instant.now());
        Mockito.when(mockUserStore.getUser("addedUser")).thenReturn(addedUser);

        Mockito.when(mockRequest.getParameter("addNewUser")).thenReturn("notNull");
        Mockito.when(mockRequest.getParameter("newUser")).thenReturn("addedUser");

        chatAddUserServlet.doPost(mockRequest, mockResponse);

        Assert.assertNotNull(mockRequest.getParameter("addNewUser"));
        Mockito.verify(mockUserStore).getUser("addedUser");

        Assert.assertEquals(fakeConversation.isUserInConversation("fakeUser"), true);
        // make sure the added user is in the conversation as well
        Assert.assertEquals(fakeConversation.isUserInConversation("addedUser"), true);

        Mockito.verify(mockSession).setAttribute("addNewUserMessage", "Added new user to the conversation!");
        Mockito.verify(mockResponse).sendRedirect("/chat/" + fakeConversation.getTitle());
    }

}
