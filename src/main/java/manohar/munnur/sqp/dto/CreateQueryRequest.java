package manohar.munnur.sqp.dto;

import lombok.Data;

@Data
public class CreateQueryRequest {
    private String createdByUserId;     // id of student or faculty
    private String createdByRole;     // "STUDENT" or "FACULTY"
    private String title;
    private String description;
    private String priority;          // "LOW", "MEDIUM", "HIGH", "URGENT"
}
