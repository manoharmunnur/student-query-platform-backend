package manohar.munnur.sqp.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AnswerDto {
    private Long id;
    private Long queryId;
    private String answeredByUserId;
    private String answeredByRole;
    private String content;
    private Boolean accepted;
    private String reviewedByFacultyId;
    private LocalDateTime reviewedAt;
    private int voteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
