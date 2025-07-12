package proxy;

import config.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * Proxy class to handle sending emails using Jakarta Mail API.
 * This class is responsible for all external email-related communication.
 */
public class EmailProxy {

    /**
     * Sends an email with the given subject and content to the specified recipient.
     *
     * @param toEmail recipient's email address
     * @param subject subject of the email
     * @param content content/body of the email
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendEmail(String toEmail, String subject, String content) {
        // Step 1: Set SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");                         // Enable authentication
        props.put("mail.smtp.starttls.enable", "true");              // Enable STARTTLS
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);          // SMTP host (e.g., smtp.gmail.com)
        props.put("mail.smtp.port", String.valueOf(EmailConfig.SMTP_PORT)); // SMTP port (e.g., 587)

        // Step 2: Create authenticated mail session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Use email and app password from config
                return new PasswordAuthentication(EmailConfig.USERNAME, EmailConfig.PASSWORD);
            }
        });

        try {
            // Step 3: Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.USERNAME));              // Sender
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // Recipient
            message.setSubject(subject);                                             // Subject
            message.setText(content);                                                // Plain text content

            // Step 4: Send the email
            Transport.send(message);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace(); // Log error for debugging
            return false;
        }
    }

    /**
     * Sends a verification OTP email to the user during registration or password reset.
     *
     * @param toEmail recipient's email address
     * @param otpCode OTP code to send
     * @return true if email was sent successfully, false otherwise
     */
    public boolean sendOtpEmail(String toEmail, String otpCode, String otpPurpose) {
        String subject;
        String content;

        switch (otpPurpose) {
            case "register":
                subject = "Verify Your Account Registration";
                content = "Thank you for registering an account with us!\n"
                        + "If you did not make this request, simply ignore this email.\n\n"
                        + "Your OTP verification code is: " + otpCode + "\n"
                        + "Please enter this code within 1 minute to complete your registration.";
                break;

            case "setPassword":
                subject = "Reset Your Password";
                content = "We received a request to reset your password.\n"
                        + "If you did not make this request, simply ignore this email.\n\n"
                        + "Your OTP verification code is: " + otpCode + "\n"
                        + "Please enter this code within 1 minute to proceed with resetting your password.";
                break;

            case "verifyNewEmail":
                subject = "Verify Your New Email Address";
                content = "You requested to change the email address associated with your account.\n"
                        + "If you did not make this request, simply ignore this email.\n\n"
                        + "Your OTP verification code is: " + otpCode + "\n"
                        + "Please enter this code within 1 minute to confirm your new email address.";
                break;

            default:
                subject = "Your OTP Verification Code";
                content = "If you did not make this request, simply ignore this email."
                        + "Your OTP code is: " + otpCode + "\n"
                        + "This code is valid for 1 minute.\n\n";
                break;
        }

        return sendEmail(toEmail, subject, content);
    }

}
