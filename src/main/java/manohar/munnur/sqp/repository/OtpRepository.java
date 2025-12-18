package manohar.munnur.sqp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import manohar.munnur.sqp.entity.OtpVerification;

public interface OtpRepository extends JpaRepository<OtpVerification, Long> {

    // Fetch OTP using userId (email or phone)
    Optional<OtpVerification> findByUserId(String userId);

    // Fetch latest OTP for a given user
    Optional<OtpVerification> findTopByUserIdOrderByCreatedAtDesc(String userId);

    // For forgot password reset-token
    Optional<OtpVerification> findByResetToken(String resetToken);

    // Custom query (optional)
    @Query("SELECT o FROM otp_verification o WHERE o.userId = :userId")
    Optional<OtpVerification> findOtp(@Param("userId") String userId);
}
