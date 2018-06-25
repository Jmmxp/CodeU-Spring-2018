package codeu.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
	
//	@Test
//	public void testDoPost_UpdateAboutMe() throws IOException, PersistentDataStoreException, ServletException {
//
//		Profile profile = new Profile(UUID.randomUUID(),
//				"user", "Profile About Me");
//
//		HashMap<String, String> profiles = new HashMap<String, String>();
//		profiles.put("user", "Profile About Me");
//
//		ProfileStore store = Mockito.spy(ProfileStore.class);
//
//		Mockito.when(mockRequest.getRequestURI()).thenReturn("/profile/user");
//		Mockito.when(mockSession.getAttribute("user")).thenReturn("user");
//
//		profileServlet.doGet(mockRequest, mockResponse);
//
//		Mockito.when(mockRequest.getParameter("description")).thenReturn("Profile About Me changed");
//
//		PersistentStorageAgent mockStorageAgent = Mockito.mock(PersistentStorageAgent.class);
//		Mockito.when(mockStorageAgent.loadProfiles()).thenReturn(profiles);
//		ProfileStore mockProfileStore = Mockito.mock(ProfileStore.getTestInstance(mockStorageAgent).getClass());
//		Mockito.when(mockProfileStore.getProfileText("user")).thenReturn("Profile About Me");
//
//		store.setProfiles(profiles);
//
//		//mockProfileStore.setProfiles(profiles);
//
//		profileServlet.setProfileStore(store);
//
//		// Assert.assertEquals(mockRequest.getParameter("aboutMe"), "Profile About Me");
//		Mockito.when(mockRequest.getParameter("aboutMe")).thenReturn("Profile About Me");
//		profileServlet.doPost(mockRequest, mockResponse);
//
//		Mockito.verify(mockResponse).sendRedirect("/profile/user");
//		Mockito.verify(mockRequest).getParameter("aboutMe").equals("Profile About Me changed");
//	}
	
	
	
}