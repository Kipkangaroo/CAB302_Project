package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

/**
 * Unit tests for OtpDAO, covering expected behaviour and edge cases using isolated or in-memory dependencies where appropriate.
 *
 * @author LockedIn Team
 * @version 1.0
 */
class OtpDAOTest {

    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private OtpDAO otpDAO;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        otpDAO = new OtpDAO(conn);
    }


    /**
     * Verifies saveOrReplaceOtp: persists Code.
     */
    @Test
    void saveOrReplaceOtp_persistsCode() {
        otpDAO.saveOrReplaceOtp("user@test.com", 123456);
        assertEquals(Optional.of(123456), otpDAO.getOtpCode("user@test.com"));
    }


    /**
     * Verifies getOtpCode: returns Empty when Not Saved.
     */
    @Test
    void getOtpCode_returnsEmpty_whenNotSaved() {
        assertTrue(otpDAO.getOtpCode("missing@test.com").isEmpty());
    }


    /**
     * Verifies verifyOtp: returns True for Matching Code.
     */
    @Test
    void verifyOtp_returnsTrue_forMatchingCode() {
        otpDAO.saveOrReplaceOtp("a@test.com", 111111);
        assertTrue(otpDAO.verifyOtp("a@test.com", 111111));
    }


    /**
     * Verifies verifyOtp: returns False for Wrong Code.
     */
    @Test
    void verifyOtp_returnsFalse_forWrongCode() {
        otpDAO.saveOrReplaceOtp("b@test.com", 222222);
        assertFalse(otpDAO.verifyOtp("b@test.com", 999999));
    }


    /**
     * Verifies verifyOtp: returns False when Email Unknown.
     */
    @Test
    void verifyOtp_returnsFalse_whenEmailUnknown() {
        assertFalse(otpDAO.verifyOtp("ghost@test.com", 123456));
    }


    /**
     * Verifies saveOrReplaceOtp: overwrites Previous Code.
     */
    @Test
    void saveOrReplaceOtp_overwritesPreviousCode() {
        otpDAO.saveOrReplaceOtp("c@test.com", 100);
        otpDAO.saveOrReplaceOtp("c@test.com", 200);
        assertEquals(Optional.of(200), otpDAO.getOtpCode("c@test.com"));
    }


    /**
     * Verifies deleteOtp: removes Stored Code.
     */
    @Test
    void deleteOtp_removesStoredCode() {
        otpDAO.saveOrReplaceOtp("d@test.com", 333333);
        otpDAO.deleteOtp("d@test.com");
        assertTrue(otpDAO.getOtpCode("d@test.com").isEmpty());
    }


    /**
     * Verifies getOtpCode: isolates Emails.
     */
    @Test
    void getOtpCode_isolatesEmails() {
        otpDAO.saveOrReplaceOtp("one@test.com", 1);
        otpDAO.saveOrReplaceOtp("two@test.com", 2);
        assertEquals(Optional.of(1), otpDAO.getOtpCode("one@test.com"));
        assertEquals(Optional.of(2), otpDAO.getOtpCode("two@test.com"));
    }
}
