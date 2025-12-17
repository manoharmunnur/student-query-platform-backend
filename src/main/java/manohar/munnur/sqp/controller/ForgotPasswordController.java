package manohar.munnur.sqp.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.entity.Faculty;
import manohar.munnur.sqp.entity.OtpVerification;
import manohar.munnur.sqp.entity.Student;
import manohar.munnur.sqp.repository.FacultyRepository;
import manohar.munnur.sqp.repository.OtpRepository;
import manohar.munnur.sqp.repository.StudentRepository;
import manohar.munnur.sqp.service.OtpService;

@RestController
@RequestMapping("/forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final StudentRepository studentRepo;
    private final FacultyRepository facultyRepo;
    private final OtpRepository otpRepo;
    private final OtpService otpService;

    // ---------------------------
    // STEP 1: Request OTP
    // ---------------------------
    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestParam String email) {

        Optional<Student> student = studentRepo.findByEmail(email);
        Optional<Faculty> faculty = facultyRepo.findByEmail(email);

        if (student.isEmpty() && faculty.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Email not found");
        }

        // Generate OTP
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(email, otp);

        // Save to DB
        OtpVerification otpData = new OtpVerification();
        otpData.setEmailOtp(otp);
        otpData.setCreatedAt(LocalDateTime.now());
        otpData.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otpData.setUserId(email); // store email as userId reference  
        otpData.setResetToken(null); // clear previous token

        otpRepo.save(otpData);

        return ResponseEntity.ok("OTP sent to email");
    }

    // ---------------------------
    // STEP 2: Verify OTP
    // ---------------------------
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                       @RequestParam String otp) {

        // Get latest OTP entry for this email
        OtpVerification otpRecord = otpRepo.findTopByUserIdOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!otpRecord.getEmailOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP expired");
        }

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        otpRecord.setResetToken(resetToken);
        otpRepo.save(otpRecord);

        return ResponseEntity.ok(Map.of(
                "resetToken", resetToken,
                "message", "OTP verified successfully"
        ));
    }

    // ---------------------------
    // STEP 3: Reset Password
    // ---------------------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String resetToken,
            @RequestParam String newPassword) {

        OtpVerification otpRecord = otpRepo.findByResetToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        String email = otpRecord.getUserId(); // stored earlier

        // Update Student password
        studentRepo.findByEmail(email).ifPresent(student -> {
            student.setPassword(newPassword);
            studentRepo.save(student);
        });

        // Update Faculty password
        facultyRepo.findByEmail(email).ifPresent(faculty -> {
            faculty.setPassword(newPassword);
            facultyRepo.save(faculty);
        });

        // Invalidate token
        otpRecord.setResetToken(null);
        otpRepo.save(otpRecord);

        return ResponseEntity.ok("Password reset successful");
    }
}
