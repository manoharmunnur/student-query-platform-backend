package manohar.munnur.sqp.mapper;

import manohar.munnur.sqp.dto.AnswerDto;
import manohar.munnur.sqp.entity.queries.Answer;
import manohar.munnur.sqp.entity.queries.Query;

public class AnswerMapper {

    // Entity -> DTO
    public static AnswerDto mapToAnswerDto(Answer a) {
        if (a == null) return null;

        AnswerDto dto = new AnswerDto();
        dto.setId(a.getId());
        dto.setQueryId(a.getQuery() != null ? a.getQuery().getId() : null);
        dto.setAnsweredByUserId(a.getAnsweredByUserId());
        dto.setAnsweredByRole(a.getAnsweredByRole());
        dto.setContent(a.getContent());
        dto.setAccepted(a.getAccepted());
        dto.setReviewedByFacultyId(a.getReviewedByFacultyId());
        dto.setReviewedAt(a.getReviewedAt());
        dto.setVoteCount(a.getVoteCount());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());
        return dto;
    }

    // DTO -> Entity (for updates; requires Query object)
    public static Answer mapToAnswer(AnswerDto dto, Query query) {
        if (dto == null) return null;

        Answer answer = new Answer();
        answer.setId(dto.getId());  // null for create, set for update
        answer.setQuery(query);
        answer.setAnsweredByUserId(dto.getAnsweredByUserId());
        answer.setAnsweredByRole(dto.getAnsweredByRole());
        answer.setContent(dto.getContent());
        answer.setAccepted(dto.getAccepted());
        answer.setReviewedByFacultyId(dto.getReviewedByFacultyId());
        answer.setReviewedAt(dto.getReviewedAt());
        answer.setVoteCount(dto.getVoteCount());
        answer.setCreatedAt(dto.getCreatedAt());
        answer.setUpdatedAt(dto.getUpdatedAt());
        return answer;
    }
}
