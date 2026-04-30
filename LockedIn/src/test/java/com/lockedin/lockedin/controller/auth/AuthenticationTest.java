package com.lockedin.lockedin.controller.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    // ---- isValidEmail ----

    @Test
    void isValidEmail_acceptsTypicalEmail() {
        assertTrue(Authentication.isValidEmail("user@example.com"));
    }

    @Test
    void isValidEmail_acceptsSubdomain() {
        assertTrue(Authentication.isValidEmail("user@mail.example.org"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutAt() {
        assertFalse(Authentication.isValidEmail("userexample.com"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutDomain() {
        assertFalse(Authentication.isValidEmail("user@"));
    }

    @Test
    void isValidEmail_rejectsEmailWithoutTld() {
        assertFalse(Authentication.isValidEmail("user@example"));
    }

    @Test
    void isValidEmail_rejectsEmptyString() {
        assertFalse(Authentication.isValidEmail(""));
    }

    // ---- isValidPassword ----

    @Test
    void isValidPassword_acceptsValidPassword() {
        assertTrue(Authentication.isValidPassword("Secure@123"));
    }

    @Test
    void isValidPassword_rejectsTooShort() {
        assertFalse(Authentication.isValidPassword("Ab!1234"));
    }

    @Test
    void isValidPassword_rejectsNoUppercase() {
        assertFalse(Authentication.isValidPassword("password1!"));
    }

    @Test
    void isValidPassword_rejectsNoLowercase() {
        assertFalse(Authentication.isValidPassword("PASSWORD1!"));
    }

    @Test
    void isValidPassword_rejectsNoSpecialCharacter() {
        assertFalse(Authentication.isValidPassword("Password123"));
    }
}