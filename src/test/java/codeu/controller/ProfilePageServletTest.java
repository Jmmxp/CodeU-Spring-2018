package codeu.controller;

import static codeu.model.data.Conversation.ConversationType;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import codeu.model.data.Conversation;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.UserStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import codeu.helper.ProfileHelper;
import codeu.model.data.Profile;
import codeu.model.store.basic.ProfileStore;
import codeu.model.store.persistence.PersistentDataStoreException;
import codeu.model.store.persistence.PersistentStorageAgent;

/**
 * Testing class for the ProfilePageServlet
 *
 */
public class ProfilePageServletTest {
	
	private ProfilePageServlet profileServlet;
	private HttpServletRequest mockRequest;
	private HttpSession mockSession;
	private HttpServletResponse mockResponse;
	private RequestDispatcher mockRequestDispatcher;
	private PersistentStorageAgent mockPersistentStorageAgent;
	private ConversationStore fakeConversationStore;
	private UserStore fakeUserStore;
	private ProfileStore fakeProfileStore;
	
	@Before
	public void setup() {
		profileServlet = new ProfilePageServlet();
		
		mockRequest = Mockito.mock(HttpServletRequest.class);
		mockSession = Mockito.mock(HttpSession.class);
		Mockito.when(mockRequest.getSession()).thenReturn(mockSession);
		
		mockResponse = Mockito.mock(HttpServletResponse.class);
		mockRequestDispatcher = Mockito.mock(RequestDispatcher.class);
		Mockito.when(mockRequest.getRequestDispatcher("/WEB-INF/view/profile.jsp"))
			.thenReturn(mockRequestDispatcher);

		mockPersistentStorageAgent = Mockito.mock(PersistentStorageAgent.class);

	}
	
	@Test
	public void testDoGet_UserCheckingOtherUsersProfile() throws ServletException, IOException {
		Mockito.when(mockSession.getAttribute("user")).thenReturn("notSameUser");
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/user");
		
		profileServlet.doGet(mockRequest, mockResponse);
		
		Assert.assertFalse(ProfileHelper.isSameUser((String) mockSession.getAttribute("user"), 
				mockRequest.getRequestURI().substring("/profile/".length())));
		
		Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
	}
	
	@Test
	public void testDoGet_UserCheckingTheirOwnProfile() throws ServletException, IOException {
		Mockito.when(mockSession.getAttribute("user")).thenReturn("sameUser");
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/sameUser");
		
		profileServlet.doGet(mockRequest, mockResponse);
		
		Assert.assertTrue(ProfileHelper.isSameUser((String) mockSession.getAttribute("user"), 
				mockRequest.getRequestURI().substring("/profile/".length())));
		
		Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
	}
	
	@Test
	public void testDoGet_UserNotLoggedInCheckingProfile() throws ServletException, IOException {
		Mockito.when(mockSession.getAttribute("user")).thenReturn(null);
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/someUser");
		
		profileServlet.doGet(mockRequest, mockResponse);
		
		Assert.assertFalse(ProfileHelper.isSameUser((String) mockSession.getAttribute("user"), 
				mockRequest.getRequestURI().substring("/profile/".length())));
		
		Mockito.verify(mockRequestDispatcher).forward(mockRequest, mockResponse);
	}
	
	@Test
	public void testDoGet_ProfileDoesNotExist() throws ServletException, IOException {
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/userDoesNotExist");
		
		profileServlet.doGet(mockRequest, mockResponse);
		
		Mockito.verify(mockResponse).sendRedirect("/login");
	}
  
  public void testDoPost_UpdateAboutMe() throws IOException, PersistentDataStoreException, ServletException {
		fakeProfileStore = ProfileStore.getTestInstance(mockPersistentStorageAgent);

		Profile profile = new Profile(UUID.randomUUID(), "vasu", "Google Engineer");

		fakeProfileStore.addProfile(profile);
		profileServlet.setProfileStore(fakeProfileStore);

		Assert.assertEquals(fakeProfileStore.getProfileText("vasu"), "Google Engineer");

		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/vasu");
		// the description parameter holds the text the user is trying to update their 'About Me' to.
		Mockito.when(mockRequest.getParameter("description"))
				.thenReturn("Google Engineer and CodeU Project Advisor");

		profileServlet.doPost(mockRequest, mockResponse);

		Assert.assertEquals(fakeProfileStore.getProfileText("vasu"),
				"Google Engineer and CodeU Project Advisor");
		Mockito.verify(mockResponse).sendRedirect("/profile/vasu");
	}

	@Test
	public void testDoPost_DirectMessageNotLoggedIn() throws IOException {
		Mockito.when(mockRequest.getParameter("messageUserButton")).thenReturn("notNull");
		Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn(null);
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/Cynthia");

		profileServlet.doPost(mockRequest, mockResponse);

		Mockito.verify(mockResponse).sendRedirect("/login");
	}

	@Test
	public void testDoPost_DirectMessageExists() throws IOException {
		// Check for the DM starting between Justin and Vasu
		Mockito.when(mockRequest.getParameter("messageUserButton")).thenReturn("notNull");
		Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("Justin");
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/Vasu");

		fakeConversationStore = ConversationStore.getTestInstance(mockPersistentStorageAgent);

		// Set up the conversation between Justin and Vasu
		User userOne = new User(UUID.randomUUID(), "Justin", "testHash", Instant.now());
		User userTwo = new User(UUID.randomUUID(), "Vasu", "testHash2", Instant.now());
		List<User> users = new ArrayList<>();
		users.add(userOne);
		users.add(userTwo);
		Conversation conversation = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "testTitle",
				Instant.now(), users, ConversationType.DIRECT);

		fakeConversationStore.addConversation(conversation);
		Assert.assertEquals(fakeConversationStore.getNumConversations(), 1);

		profileServlet.setConversationStore(fakeConversationStore);

		profileServlet.doPost(mockRequest, mockResponse);

		// check that there exists the same convo between the two users of the DM
		Conversation conversationTwo = fakeConversationStore.getDirectMessageWithUsers("Justin", "Vasu");
		Assert.assertNotNull(conversation);
		Assert.assertEquals(conversation, conversationTwo);

		Assert.assertEquals(fakeConversationStore.getNumConversations(), 1);

		// double check that the two users are the only two part of the DM
		Assert.assertEquals(conversationTwo.getNumUsers(), 2);
		Assert.assertEquals(conversationTwo.isUserInConversation("Justin"), true);
		Assert.assertEquals(conversationTwo.isUserInConversation("Vasu"), true);

		Mockito.verify(mockResponse).sendRedirect("/chat/" + conversationTwo.getTitle());
	}

	@Test
	public void testDoPost_DirectMessageDoesNotExist() throws IOException {
		// Check for the DM starting between Justin and Vasu
		Mockito.when(mockRequest.getParameter("messageUserButton")).thenReturn("notNull");
		Mockito.when(mockRequest.getSession().getAttribute("user")).thenReturn("Justin");
		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/Vasu");

		fakeConversationStore = ConversationStore.getTestInstance(mockPersistentStorageAgent);
		fakeUserStore = UserStore.getTestInstance(mockPersistentStorageAgent);

		User userOne = new User(UUID.randomUUID(), "Justin", "testHash", Instant.now());
		User userTwo = new User(UUID.randomUUID(), "Cynthia", "testHash2", Instant.now());
		User userThree = new User(UUID.randomUUID(), "Vasu", "testHash3", Instant.now());
		List<User> users = new ArrayList<>();
		users.add(userOne);
		users.add(userTwo);
		Conversation conversation = new Conversation(UUID.randomUUID(), UUID.randomUUID(), "testTitle",
				Instant.now(), users, ConversationType.DIRECT);

		fakeConversationStore.addConversation(conversation);
		Assert.assertEquals(fakeConversationStore.getNumConversations(), 1);

		fakeUserStore.addUser(userOne);
		fakeUserStore.addUser(userTwo);
		fakeUserStore.addUser(userThree);
		Assert.assertEquals(fakeUserStore.getNumUsers(), 3);

		// check that there doesn't exist a convo between the two users of the DM
		Assert.assertNull(fakeConversationStore.getDirectMessageWithUsers("Justin", "Vasu"));

		profileServlet.setConversationStore(fakeConversationStore);
		profileServlet.setUserStore(fakeUserStore);

		profileServlet.doPost(mockRequest, mockResponse);

		// check that the new DM conversation is created
		Assert.assertEquals(fakeConversationStore.getNumConversations(), 2);

		Conversation conversationTwo = fakeConversationStore.getDirectMessageWithUsers("Justin", "Vasu");
		Assert.assertNotNull(conversationTwo);

		// check that the two users are the only two part of the new DM
		Assert.assertEquals(conversationTwo.getNumUsers(), 2);
		Assert.assertEquals(conversationTwo.isUserInConversation("Justin"), true);
		Assert.assertEquals(conversationTwo.isUserInConversation("Vasu"), true);

		Mockito.verify(mockResponse).sendRedirect("/chat/" + conversationTwo.getTitle());
	}

}