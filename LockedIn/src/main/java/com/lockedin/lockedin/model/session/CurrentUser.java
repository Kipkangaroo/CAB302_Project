package com.lockedin.lockedin.model.session;

import com.lockedin.lockedin.model.entity.User;

public class CurrentUser {
    private static User user;

    private CurrentUser() {}

    public static void set(User u) { user = u; }
    public static User get() { return user; }
    public static int getId() { return user.getId(); }
    public static void clear() { user = null; }

    public static User getUser() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }
}
