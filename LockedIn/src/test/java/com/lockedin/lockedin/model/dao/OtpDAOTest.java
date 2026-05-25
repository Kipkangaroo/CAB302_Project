package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

class OtpDAOTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private OtpDAO otpDAO;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        otpDAO = new OtpDAO(conn);
    }

    @Test
    void saveOrReplaceOtp_persistsCode() {
        otpDAO.saveOrReplaceOtp("user@test.com", 123456);
        assertEquals(Optional.of(123456), otpDAO.getOtpCode("user@test.com"));
    }

    @Test
    void getOtpCode_returnsEmpty_whenNotSaved() {
        assertTrue(otpDAO.getOtpCode("missing@test.com").isEmpty());
    }

    @Test
    void verifyOtp_returnsTrue_forMatchingCode() {
        otpDAO.saveOrReplaceOtp("a@test.com", 111111);
        assertTrue(otpDAO.verifyOtp("a@test.com", 111111));
    }

    @Test
    void verifyOtp_returnsFalse_forWrongCode() {
        otpDAO.saveOrReplaceOtp("b@test.com", 222222);
        assertFalse(otpDAO.verifyOtp("b@test.com", 999999));
    }

    @Test
    void verifyOtp_returnsFalse_whenEmailUnknown() {
        assertFalse(otpDAO.verifyOtp("ghost@test.com", 123456));
    }

    @Test
    void saveOrReplaceOtp_overwritesPreviousCode() {
        otpDAO.saveOrReplaceOtp("c@test.com", 100);
        otpDAO.saveOrReplaceOtp("c@test.com", 200);
        assertEquals(Optional.of(200), otpDAO.getOtpCode("c@test.com"));
    }

    @Test
    void deleteOtp_removesStoredCode() {
        otpDAO.saveOrReplaceOtp("d@test.com", 333333);
        otpDAO.deleteOtp("d@test.com");
        assertTrue(otpDAO.getOtpCode("d@test.com").isEmpty());
    }

    @Test
    void getOtpCode_isolatesEmails() {
        otpDAO.saveOrReplaceOtp("one@test.com", 1);
        otpDAO.saveOrReplaceOtp("two@test.com", 2);
        assertEquals(Optional.of(1), otpDAO.getOtpCode("one@test.com"));
        assertEquals(Optional.of(2), otpDAO.getOtpCode("two@test.com"));
    }
}
