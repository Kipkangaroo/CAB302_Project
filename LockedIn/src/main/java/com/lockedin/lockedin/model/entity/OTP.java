package com.lockedin.lockedin.model.entity.user;

import com.lockedin.lockedin.model.dao.OtpDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.service.SendEmail;

import java.io.IOException;
import java.util.Random;

public class OTP {
    private String email;
    private int otpCode;
    private UserDAO userDAO;
    private OtpDAO otpDAO;

    public OTP(String email) {
        this.email = email;
        this.userDAO = new UserDAO();
        this.otpDAO = new OtpDAO();
        generateOtpCode();
    }

    public String getEmail() {
        return email;
    }

    public int getOtpCode() {
        return otpCode;
    }

    private void generateOtpCode() {
        Random random = new Random();
        otpCode = random.nextInt(1000000);
        otpDAO.saveOrReplaceOtp(email, otpCode);
    }

    boolean verifyEmailExists(String email) {
        return userDAO.getUserByEmail(email).isPresent();
    }

    boolean verifyOtpCode(String email, int otpCode) {
        return otpDAO.verifyOtp(email, otpCode);
    }

    public void sendOtpToEmail () throws IOException {
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendOtpEmail(email, otpCode);
    }
}
