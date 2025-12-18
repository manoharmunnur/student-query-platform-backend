package manohar.munnur.sqp.entity.queries;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "answer_votes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"answer_id", "faculty_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which answer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    // which faculty voted
    @Column(name = "faculty_id", nullable = false, length = 191)
    private String facultyId;

    // +1 (upvote), -1 (downvote) if you want both
    @Column(nullable = false)
    private int value;   // 1 for upvote, -1 for downvote (or just 1 if only upvote)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
