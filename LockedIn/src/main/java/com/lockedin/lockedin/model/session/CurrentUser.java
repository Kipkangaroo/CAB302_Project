package com.lockedin.lockedin.model.session;

import com.lockedin.lockedin.model.entity.user.User;

/**
 * Provides current user functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public class CurrentUser {
    private static User activeUser;

        /**
     * Constructs a CurrentUser using default application dependencies.
     */
    private CurrentUser() {
    }

    /**
     * Stores the active session user.
     *
     * @param user the user to set as the current session user
     */
    public static void set(User user) {
        activeUser = user;
    }

            /**
     * Returns the active session user.
     * 
     * @return the current session user, or null if none is set
     */
    public static User get() {
        return activeUser;
    }

            /**
     * Returns the id.
     * @return id
     */
    public static int getId() {
        return activeUser.getId();
    }

    /**
     * Clears the active session user.
     */
    public static void clear() {
        activeUser = null;
    }

            /**
     * Returns the user.
     * @return user
     */
    public static User getActiveUser() {
        return activeUser;
    }
}
