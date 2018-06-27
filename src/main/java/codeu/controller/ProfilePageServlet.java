package codeu.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeu.model.data.Conversation;
import codeu.model.data.Profile;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.ProfileStore;
import codeu.model.store.basic.UserStore;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Servlet class responsible for a user's profile page. */
public class ProfilePageServlet extends HttpServlet {

	/** Store class that gives access to Users. */
	  private ProfileStore profileStore;

	  /**
	   * Set up state for handling profile-related requests. 
	   */
	  @Override
	  public void init() throws ServletException {
	    super.init();
	    setProfileStore(ProfileStore.getInstance());
	  }

	  /**
	   * Sets the ProfileStore used by this servlet. This function provides a common setup method for use
	   * by the test framework or the servlet's init() function.
	   */
	  void setProfileStore(ProfileStore profileStore) {
	    this.profileStore = profileStore;
	  }
	
	/**
	 * This function fires when a user navigates to a profile page. It displays a
	 * user's About Me section and depending on if the profile is the current
	 * user's, allows editing of the About Me section.
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String user = (String) request.getSession().getAttribute("user");
		String profileName = request.getRequestURI().substring("/profile/".length());

		User profileOwner = UserStore.getInstance().getUser(profileName);

		// user does not exist
		if (profileOwner == null) {
			if (user != null)
				response.sendRedirect("/profile/" + user);
			else
				response.sendRedirect("/login");
		}

		request.setAttribute("profileName", profileName);

		request.getRequestDispatcher("/WEB-INF/view/profile.jsp").forward(request, response);
		return;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String user = (String) request.getSession().getAttribute("user");
		String profileOwner = request.getRequestURI().substring("/profile/".length());
		String text = request.getParameter("description");

		if (request.getParameter("messageUserButton") != null) {
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			UserStore userStore = UserStore.getInstance();
			ConversationStore conversationStore = ConversationStore.getInstance();

			Conversation directMessageConversation = conversationStore.getDirectMessageWithUsers(user, profileOwner);
			String conversationTitle;

			if (directMessageConversation == null) {
				// the DM does not exist yet, so create a new conversation for it
				UUID id = UUID.randomUUID();
				UUID ownerId = userStore.getUser(user).getId();
				conversationTitle = id.toString();
				Conversation conversation = new Conversation(id, ownerId, conversationTitle, Instant.now());
				conversation.addUser(user);
				conversation.addUser(profileOwner);
				conversationStore.addConversation(conversation);
			} else {
				conversationTitle = directMessageConversation.getTitle();
			}

			response.sendRedirect("/chat/" + conversationTitle);
			return;
		}


		ProfileStore.getInstance()
				.addProfile(new Profile(UUID.randomUUID(),
						profileOwner,
						text));
		ProfileStore.getInstance().setProfileText(profileOwner, text);
		
		response.sendRedirect("/profile/" + profileOwner);
	}

}
