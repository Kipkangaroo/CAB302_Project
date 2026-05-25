package com.lockedin.lockedin.model.entity.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OtpTest {

    @Test
    void getEmail_returnsConstructorEmail() {
        Otp otp = new Otp("reset@test.com");
        assertEquals("reset@test.com", otp.getEmail());
    }

    @Test
    void getOtpCode_isNonNegative() {
        Otp otp = new Otp("a@test.com");
        assertTrue(otp.getOtpCode() >= 0);
    }

    @Test
    void getOtpCode_isLessThanOneMillion() {
        Otp otp = new Otp("b@test.com");
        assertTrue(otp.getOtpCode() < 1_000_000);
    }

    @Test
    void verifyOtpCode_returnsTrue_forStoredCode() {
        Otp otp = new Otp("stored@test.com");
        assertTrue(otp.verifyOtpCode("stored@test.com", otp.getOtpCode()));
    }

    @Test
    void verifyOtpCode_returnsFalse_forWrongCode() {
        Otp otp = new Otp("wrong@test.com");
        assertFalse(otp.verifyOtpCode("wrong@test.com", otp.getOtpCode() + 1));
    }

    @Test
    void verifyOtpCode_returnsFalse_forDifferentEmail() {
        Otp otp = new Otp("mine@test.com");
        assertFalse(otp.verifyOtpCode("other@test.com", otp.getOtpCode()));
    }

    @Test
    void verifyEmailExists_returnsFalse_forUnknownEmail() {
        Otp otp = new Otp("probe@test.com");
        assertFalse(otp.verifyEmailExists("definitely-not-registered-12345@test.com"));
    }

    @Test
    void sendOtpToEmail_doesNotThrowWhenInvoked() {
        Otp otp = new Otp("thread@test.com");
        assertDoesNotThrow(otp::sendOtpToEmail);
    }
}
