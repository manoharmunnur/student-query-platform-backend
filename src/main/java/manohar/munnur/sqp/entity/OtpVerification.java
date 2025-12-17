package manohar.munnur.sqp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String emailOtp;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    @Column
    private String resetToken;  // for forgot password flow
}
