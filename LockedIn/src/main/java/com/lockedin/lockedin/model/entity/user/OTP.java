package com.lockedin.lockedin.model.entity.user;

import com.lockedin.lockedin.model.dao.OtpDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.service.SendEmail;

import java.util.Random;

/**
 * Provides otp functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public class Otp {
    private String email;
    private int otpCode;
    private UserDAO userDAO;
    private OtpDAO otpDAO;

    public Otp(String email) {
        this.email = email;
        this.userDAO = new UserDAO();
        this.otpDAO = new OtpDAO();
        generateOtpCode();
    }

            /**
     * Returns the email.
     * @return email
     */
    public String getEmail() {
        return email;
    }

            /**
     * Returns the otp code.
     * @return otp code
     */
    public int getOtpCode() {
        return otpCode;
    }

        /**
     * Generate otp code.
     */
    private void generateOtpCode() {
        Random random = new Random();
        otpCode = random.nextInt(1000000);
        otpDAO.saveOrReplaceOtp(email, otpCode);
    }

        /**
     * Verify email exists.
     * @param email email
     * @return true if the operation succeeds; otherwise false.
     */
    boolean verifyEmailExists(String email) {
        return userDAO.getUserByEmail(email).isPresent();
    }

        /**
     * Verify otp code.
     * @param email email
     * @param otpCode The otp code.
     * @return true if the operation succeeds; otherwise false.
     */
    boolean verifyOtpCode(String email, int otpCode) {
        return otpDAO.verifyOtp(email, otpCode);
    }

    /**
     * Sends the OTP email on a background thread.
     */
    public void sendOtpToEmail() {
        new Thread(() -> {
            try {
                new SendEmail().sendOtpEmail(email, otpCode);
            } catch (RuntimeException ignored) {
            }
        }).start();
    }
}
