package manohar.munnur.sqp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryDto {
    private Long id;
    private String createdByUserId;
    private String createdByRole;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String assignedFacultyId;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}

