package manohar.munnur.sqp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "temp_registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TempRegistration {

    @Id
    @Column(length = 191)
    private String id; // UUID string

    // "STUDENT" or "FACULTY"
    private String userType;

    // email is stored for convenience (and mail sending)
    @Column(nullable = false)
    private String email;

    // JSON snapshot of DTO
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String jsonData;

    @Lob
    private byte[] image;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
