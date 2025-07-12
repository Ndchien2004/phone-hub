package service.user;

public class EmailService {
    /**
     * Generates a random 6-digit OTP code.
     */
    public String generateOtpCode() {
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);
    }
}
