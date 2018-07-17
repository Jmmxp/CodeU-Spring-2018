package codeu.controller;

import codeu.model.data.Conversation;
import codeu.model.data.Message;
import codeu.model.data.User;
import codeu.model.store.basic.ConversationStore;
import codeu.model.store.basic.MessageStore;
import codeu.model.store.basic.UserStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Servlet class responsible for adding users on the chat page for Group Conversations. */
public class ChatAddUserServlet extends HttpServlet {

    /** Store class that gives access to Conversations. */
    private ConversationStore conversationStore;

    /** Store class that gives access to Users. */
    private UserStore userStore;

    /** Set up state for handling chat requests. */
    @Override
    public void init() throws ServletException {
        super.init();
        setConversationStore(ConversationStore.getInstance());
        setUserStore(UserStore.getInstance());
    }

    /**
     * Sets the ConversationStore used by this servlet. This function provides a common setup method
     * for use by the test framework or the servlet's init() function.
     */
    void setConversationStore(ConversationStore conversationStore) {
        this.conversationStore = conversationStore;
    }

    /**
     * Sets the UserStore used by this servlet. This function provides a common setup method for use
     * by the test framework or the servlet's init() function.
     */
    void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    /**
     * This function fires when a user tries to add another user to a Group Conversation
     * It adds the user to the conversation if possible, and then redirects back to ChatServlet so that
     * it can render chat.jsp
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String requestUrl = request.getRequestURI();
        String conversationTitle = requestUrl.substring("/chat/add-user/".length());
        String newUserName = request.getParameter("new_user");

        // Sanity checks are performed by ChatServlet in doPost (which is what redirects to here)
        Conversation conversation = conversationStore.getConversationWithTitle(conversationTitle);
        User newUser = userStore.getUser(newUserName);

        if (newUser == null) {
            request.getSession().setAttribute("addNewUserMessage", "Couldn't find that user");
        } else {
            conversation.addUser(newUser);
            request.getSession().setAttribute("addNewUserMessage", "Added new user to the conversation!");

        }

        response.sendRedirect("/chat/" + conversationTitle);
        return;

    }

}
