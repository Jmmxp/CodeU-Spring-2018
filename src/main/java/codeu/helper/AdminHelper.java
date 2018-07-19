package codeu.helper;

/** Helper class containing methods pertaining to the admin page */
public class AdminHelper {

    /**
     * Determines whether or not the given username is an admin.
     * @param user representing the user currently logged into the site (null if not logged in)
     * @return boolean
     */
    public static boolean isAdmin(String user) {
        if (user == null)
            return false;

        return true;

//        String[] admins = {"cynthia", "justin", "sergio", "vasuman"};
//        for (String admin : admins) {
//            if (admin.equals(user.toLowerCase()))
//                return true;
//        }
//        return false;
    }

}
