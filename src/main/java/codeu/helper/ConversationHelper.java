package codeu.helper;

import codeu.model.data.User;

import java.util.ArrayList;
import java.util.List;

/** Helper class containing methods pertaining Conversation object instantiation */
public class ConversationHelper {

    /**
     * @param users List of User objects to convert to usernames
     * @return List of Strings, the usernames of param users
     */
    public static List<String> getUsernamesFromUsers(List<User> users) {
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            usernames.add(user.getName());
        }
        return usernames;
    }

}
