package com.lockedin.lockedin.model.session;

import com.lockedin.lockedin.model.entity.user.User;

/**
 * Provides current user functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public class CurrentUser {
    private static User user;

    /**
     * Creates a new CurrentUser.
     */
    private CurrentUser() {
    }

    /**
     * Stores the active session user.
     *
     * @param u the user to set as the current session user
     */
    public static void set(User u) {
        user = u;
    }

    /**
     * Returns the active session user.
     *
     * @return the current session user, or null if none is set
     */
    public static User get() {
        return user;
    }

    /**
     * Returns the id.
     * @return The id.
     */
    public static int getId() {
        return user.getId();
    }

    /**
     * Clears the active session user.
     */
    public static void clear() {
        user = null;
    }

    /**
     * Returns the user.
     * @return The user.
     */
    public static User getUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }
}
