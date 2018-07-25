package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;
import codeu.model.store.persistence.PersistentStorageAgent;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class AdminServletTest {

    private AdminServlet adminServlet;
    private HttpServletRequest mockRequest;
    private HttpSession mockSession;
    private HttpServletResponse mockResponse;
    private RequestDispatcher mockRequestDispatcher;
    private ConversationStore fakeConversationStore;
    private MessageStore fakeMessageStore;
    private UserStore fakeUserStore;
    private PersistentStorageAgent mockPersistentStorageAgent;

    private String adminUser = "Justin";

    @Before
    public void setup() {
        adminServlet = new AdminServlet();

        mockRequest = Mockito.mock(HttpServletRequest.class);
        mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockRequest.getSession()).thenReturn(mockSession);

        mockResponse = Mockito.mock(HttpServletResponse.class);
        mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
        Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/admin.jsp"))
                .thenReturn(mockRequestDispatcher);

        mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);

        fakeConversationStore = ConversationStore.getTestInstance(mockPersistentStorageAgent);
        for (int i = 0; i < 123; i++) {
            fakeConversationStore.addConversation(new Conversation(UUID.randomUUID(), UUID.randomUUID(),
                    "Conversation " + i, Instant.now()));
        }
        fakeMessageStore = MessageStore.getTestInstance(mockPersistentStorageAgent);
        for (int i = 0; i < 1234; i++) {
            fakeMessageStore.addMessage(new Message(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    "Message " + i, Instant.now()));
        }

        fakeUserStore = UserStore.getTestInstance(mockPersistentStorageAgent);
        for (int i = 0; i < 12345; i++) {
            fakeUserStore.addUser(new User(UUID.randomUUID(), "User " + i, "password",
                    Instant.now()));
        }

        adminServlet.setConversationStore(fakeConversationStore);
        adminServlet.setMessageStore(fakeMessageStore);
        adminServlet.setUserStore(fakeUserStore);

    }

    @Test
    public void testDoGet_UserNotLoggedIn() throws IOException, ServletException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(null);

        adminServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/login?error_message=notloggedin&post_login_redirect=/admin");
    }

    // Commented out for showcase
//    @Test
//    public void testDoGet_UserNotAdmin() throws IOException, ServletException {
//        Mockito.when(mockSession.getAttribute("user")).thenReturn("Foobar");
//
//        adminServlet.doGet(mockRequest, mockResponse);
//
//        Mockito.verify(mockSession).setAttribute("adminMessage", "You are not an admin!");
//        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
//    }

    @Test
    public void testDoGet_UserIsAdmin() throws IOException, ServletException {

        Mockito.when(mockSession.getAttribute("user")).thenReturn(adminUser);

        adminServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockSession).setAttribute("adminMessage", "Hello " + adminUser + "! Welcome to the admin page!");

        Assert.assertEquals(123, fakeConversationStore.getNumConversations());
        Assert.assertEquals(1234, fakeMessageStore.getNumMessages());
        Assert.assertEquals(12345, fakeUserStore.getNumUsers());

        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost() throws IOException, ServletException {
        adminServlet.doPost(mockRequest, mockResponse);
        Assert.assertNull(mockRequest.getParameter("deleteUsersButton"));
        Assert.assertNull(mockRequest.getParameter("deleteMessagesButton"));
        Assert.assertNull(mockRequest.getParameter("deleteConversationsButton"));

        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost_DeleteUsers() throws ServletException, IOException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(adminUser);

        Assert.assertEquals(12345, fakeUserStore.getNumUsers());
        Mockito.when(mockRequest.getParameter("deleteUsersButton")).thenReturn("notNull");

        adminServlet.doPost(mockRequest, mockResponse);

        Mockito.verify(mockResponse).sendRedirect("/logout");

        Assert.assertEquals(0, fakeUserStore.getNumUsers());
    }

    @Test
    public void testDoPost_DeleteMessages() throws ServletException, IOException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(adminUser);

        Assert.assertEquals(1234, fakeMessageStore.getNumMessages());
        Mockito.when(mockRequest.getParameter("deleteMessagesButton")).thenReturn("notNull");

        adminServlet.doPost(mockRequest, mockResponse);

        Assert.assertEquals(0, fakeMessageStore.getNumMessages());

        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

    @Test
    public void testDoPost_DeleteConversations() throws ServletException, IOException {
        Mockito.when(mockSession.getAttribute("user")).thenReturn(adminUser);

        Assert.assertEquals(123, fakeConversationStore.getNumConversations());
        Mockito.when(mockRequest.getParameter("deleteConversationsButton")).thenReturn("notNull");

        adminServlet.doPost(mockRequest, mockResponse);

        Assert.assertEquals(0, fakeConversationStore.getNumConversations());

        Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
    }

}