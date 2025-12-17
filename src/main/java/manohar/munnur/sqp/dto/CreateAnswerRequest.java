package manohar.munnur.sqp.dto;

import lombok.Data;

@Data
public class CreateAnswerRequest {
    private Long queryId;
    private String answeredByUserId;
    private String answeredByRole; // "STUDENT" or "FACULTY"
    private String content;
}
