package com.lockedin.lockedin.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

/**
 * Provides send email functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public class SendEmail {

        /**
     * Constructs a SendEmail using default application dependencies.
     */
    public SendEmail() {}

        /**
     * Send otp email.
     * @param toEmail The to email.
     * @param otpCode The otp code.
     */
    public void sendOtpEmail(String toEmail, int otpCode) {
        send(toEmail, otpCode);
    }

        /**
     * Send.
     * @param toEmail The to email.
     * @param otpCode The otp code.
     */
    private void send(String toEmail, int otpCode) {
        final String fromEmail = "tusarifb@gmail.com";
        final String fromName = "LockedIn";
        String appPassword;
        try {
            appPassword = loadAppPassword();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email credentials", e);
        }
        MailerBuilder
                .withSMTPServer("smtp.gmail.com", 587, fromEmail, appPassword)
                .buildMailer()
                .sendMail(EmailBuilder.startingBlank()
                        .from(fromName + " <" + fromEmail + ">")
                        .to(toEmail)
                        .withSubject("LockedIn OTP: " + otpCode)
                        .withPlainText(
                                "Hi there! Your OTP for LockedIn is: \n\n"
                                        + otpCode
                                        + "\n\n Please enter this code to reset your password. \n\n Thank you for using LockedIn!")
                        .buildEmail());
    }

    /**
     * Loads the SMTP application password from the bundled config resource.
     *
     * @return trimmed app password property
     * @throws IOException if the config resource is missing or unreadable
     */
    private String loadAppPassword() throws IOException {
        final String emailPasswordDir = "/com/lockedin/lockedin/service/config.file";
        Properties props = new Properties();
        try (InputStream input = getClass().getResourceAsStream(emailPasswordDir)) {
            if (input == null) {
                throw new IOException("Missing resource file: config.file");
            }
            props.load(input);
        }
        return props.getProperty("app.password").trim();
    }

}
