package codeu.helper;

import codeu.model.data.Conversation;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChatHelper {
    /**
     * Determines whether or not the user can access the given conversation and redirects to the correct page
     * @param user the user currently logged into the site (null if not logged in)
     * @param conversation the Conversation to check
     * @return boolean
     */
    public static boolean canAccess(String user, Conversation conversation, HttpServletResponse response)
            throws IOException {

        if (!conversation.isNormalConversation()) {
            if (user == null) {
                // user is not logged in, redirect them to login page
                response.sendRedirect("/login");
                return false;
            }

            if (!conversation.isUserInConversation(user)) {
                // this user is not allowed to access the conversation, redirect them to their conversations page
                response.sendRedirect("/conversations");
                return false;
            }
        }

        return true; // note that all users (even logged out) can access Normal Conversations.
    }

}
