package com.lockedin.lockedin.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class SendEmail {
    private static final String EMAIL_PASSWORD_DIR = "/com/lockedin/lockedin/service/config.file";
    private static final String FROM_EMAIL = "tusarifb@gmail.com";
    private static final String FROM_NAME = "LockedIn";
    private String APP_PASSWORD;

    public SendEmail() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream(EMAIL_PASSWORD_DIR)) {
            if (input == null) {
                throw new IOException("Missing resource file: config.file");
            }
            props.load(input);
        }
        APP_PASSWORD = props.getProperty("app.password").trim();
    }

    public void sendOtpEmail(String toEmail, int otpCode) {
        send(toEmail, otpCode);
    }

    private void send(String toEmail, int otpCode) {
        MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, FROM_EMAIL, APP_PASSWORD)
                .buildMailer()
                .sendMail(EmailBuilder.startingBlank()
                        .from(FROM_NAME + " <" + FROM_EMAIL + ">")
                        .to(toEmail)
                        .withSubject("LockedIn OTP: " + otpCode)
                        .withPlainText(
                                "Hi there! Your OTP for LockedIn is: \n\n"
                                        + otpCode
                                        + "\n\n Please enter this code to reset your password. \n\n Thank you for using LockedIn!")
                        .buildEmail());
    }

}
