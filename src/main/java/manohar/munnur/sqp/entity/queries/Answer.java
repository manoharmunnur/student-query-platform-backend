package manohar.munnur.sqp.entity.queries;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which query this answer belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "query_id", nullable = false)
    private Query query;

    // Who answered: either student or faculty
    @Column(nullable = false)
    private String answeredByUserId;      // generic user id (student/faculty)
    
    @Column(nullable = false, length = 20)
    private String answeredByRole;      // "STUDENT" or "FACULTY"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Faculty verification
    private Boolean accepted;           // null = not reviewed, true = accepted, false = rejected
    private String reviewedByFacultyId;   // which faculty accepted/rejected
    private LocalDateTime reviewedAt;

    // Voting summary for fast sorting
    @Column(nullable = false)
    private int voteCount = 0;          // sum of votes (e.g., +1 per faculty upvote)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
}
