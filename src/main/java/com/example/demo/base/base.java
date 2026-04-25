package com.example.demo.base;

import com.example.demo.database.account;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Random;

@RestController
@RequestMapping("/base")
@CrossOrigin(origins = "*")
public class base {
    public static String color = "cyan";

    private final JavaMailSender mailSender;

    public base(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody dto registrationDto) throws SQLException {
        var username = registrationDto.getUsername();
        var email = registrationDto.getEmail();
        var password = registrationDto.getPassword();

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Email: " + email);

        return account.register(username, password, email);
    }

    @GetMapping("/Color")
    public String Color() throws SQLException {
        return color;
    }

    @PostMapping("/Color")
    public String EditColor(@RequestBody String Col) throws SQLException {
        color = Col;
        System.out.println(Col);
        return color;
    }

    @PostMapping("/login")
    public String LoginUser(@RequestBody dto LogginginDto) throws SQLException {
        return account.Login(LogginginDto.getUsername(), LogginginDto.getPassword());
    }

    @GetMapping("/getEmail")
    public String getEmail(@RequestParam String username) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 getEmail called");
        System.out.println("Username: " + username);
        
        try {
            System.out.println("📞 Calling account.getEmailByUsername...");
            String userEmail = account.getEmailByUsername(username);
            
            System.out.println("✅ Query successful");
            System.out.println("Email result: " + (userEmail == null ? "NULL" : userEmail));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            if (userEmail == null || userEmail.isEmpty()) {
                return "";
            }
            return userEmail;
            
        } catch (SQLException e) {
            System.err.println("❌ DATABASE ERROR in getEmail");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            System.err.println("SQL state: " + e.getSQLState());
            e.printStackTrace();
            System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    // ── OTP ──────────────────────────────────────────────────────────────

    @PostMapping("/sendOtp")
    public String sendOtp(@RequestBody otpdto otpDto) {
        String targetEmail = otpDto.getEmail();

        if (targetEmail == null || targetEmail.trim().isEmpty()) {
            return "Email is required";
        }

        // Prevent spam / overwrite
        if (OtpStore.exists(targetEmail)) {
            return "OTP already sent. Please wait 5 minutes.";
        }

        String otp = String.format("%06d", new Random().nextInt(1_000_000));
        OtpStore.put(targetEmail, otp);

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("NEW OTP for " + targetEmail + ": " + otp);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Use MimeMessage for HTML email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(targetEmail);
            helper.setSubject("N5XT — Your OTP Code");

            // HTML content for OTP
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 400px; margin:auto; padding:20px; border:1px solid #ddd; border-radius:10px; text-align:center;'>"
                    + "<h2 style='color:#333;'>N5XT OTP</h2>"
                    + "<p>Use this code to verify your account:</p>"
                    + "<div style='font-size:24px; font-weight:bold; letter-spacing:2px; padding:10px; background-color:#f2f2f2; border-radius:5px; margin:20px 0;'>"
                    + otp
                    + "</div>"
                    + "<p style='color:#888; font-size:12px;'>This OTP expires in 5 minutes.</p>"
                    + "</div>";

            helper.setText(htmlContent, true); // 'true' = HTML

            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Mail error: " + e.getMessage());
        }

        return "OTP sent";
    }

    @PostMapping("/verifyOtp")
    public String verifyOtp(@RequestBody otpdto otpDto) {
        String email = otpDto.getEmail();
        String code = otpDto.getOtp();

        String stored = OtpStore.get(email);

        System.out.println("Verifying OTP:");
        System.out.println("Input: " + code);
        System.out.println("Stored: " + stored);

        if (OtpStore.verify(email, code)) {
            return "OTP valid";
        }

        return "Invalid or expired OTP";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody otpdto otpDto) throws SQLException {
        String targetEmail = otpDto.getEmail();
        String newPassword = otpDto.getPassword();

        if (newPassword == null || newPassword.length() < 6) {
            return "Password too short";
        }

        return account.changePassword(targetEmail, newPassword);
    }


}
