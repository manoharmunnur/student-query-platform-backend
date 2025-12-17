package manohar.munnur.sqp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.dto.CreateAnswerRequest;
import manohar.munnur.sqp.entity.queries.Answer;
import manohar.munnur.sqp.entity.queries.AnswerVote;
import manohar.munnur.sqp.entity.queries.Query;
import manohar.munnur.sqp.entity.queries.QueryStatus;
import manohar.munnur.sqp.repository.AnswerRepository;
import manohar.munnur.sqp.repository.AnswerVoteRepository;
import manohar.munnur.sqp.repository.QueryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final AnswerVoteRepository voteRepository;
    private final QueryRepository queryRepository;

    // create new answer
    @Transactional
    public Answer createAnswer(CreateAnswerRequest req) {
        Query query = queryRepository.findById(req.getQueryId())
                .orElseThrow(() -> new RuntimeException("Query not found"));

        Answer answer = Answer.builder()
                .query(query)
                .answeredByUserId(req.getAnsweredByUserId())
                .answeredByRole(req.getAnsweredByRole().toUpperCase())
                .content(req.getContent())
                .voteCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        // If faculty answered, auto‑review and resolve query
        if ("FACULTY".equalsIgnoreCase(req.getAnsweredByRole())) {
            answer.setAccepted(true);
            answer.setReviewedByFacultyId(req.getAnsweredByUserId());
            answer.setReviewedAt(LocalDateTime.now());

            query.setStatus(QueryStatus.RESOLVED);
            query.setResolvedAt(LocalDateTime.now());
            query.setUpdatedAt(LocalDateTime.now());
            query.setResponse("Query solved by faculty answer.");
        }

        return answerRepository.save(answer);
    }

    // list answers of a query sorted by votes
    public List<Answer> findAnswersByQuery(Long queryId) {
        return answerRepository.findByQueryIdOrderByVoteCountDescCreatedAtAsc(queryId);
    }

    // voting with String facultyId
    @Transactional
    public void voteAnswer(Long answerId, String facultyId, int value) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        AnswerVote existing = voteRepository
                .findByAnswerIdAndFacultyId(answerId, facultyId)
                .orElse(null);

        if (existing == null) {
            AnswerVote vote = AnswerVote.builder()
                    .answer(answer)
                    .facultyId(facultyId)
                    .value(value)
                    .createdAt(LocalDateTime.now())
                    .build();
            voteRepository.save(vote);
            answer.setVoteCount(answer.getVoteCount() + value);
        } else if (existing.getValue() != value) {
            int diff = value - existing.getValue();
            existing.setValue(value);
            voteRepository.save(existing);
            answer.setVoteCount(answer.getVoteCount() + diff);
        } else {
            voteRepository.delete(existing);
            answer.setVoteCount(answer.getVoteCount() - value);
        }

        answerRepository.save(answer);
    }

    // review student answer: accept -> RESOLVED, reject -> mark wrong
    @Transactional
    public void reviewAnswer(Long answerId, String facultyId, boolean accepted) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        answer.setAccepted(accepted);
        answer.setReviewedByFacultyId(facultyId);
        answer.setReviewedAt(LocalDateTime.now());
        answerRepository.save(answer);

        Query query = answer.getQuery();
        if (query == null) return;

        if (accepted) {
            query.setStatus(QueryStatus.RESOLVED);
            query.setResolvedAt(LocalDateTime.now());
            String msg = "Resolved by answer (ID: " + answer.getId() + ").";
            if (query.getResponse() == null || query.getResponse().isBlank()) {
                query.setResponse(msg);
            } else {
                query.setResponse(query.getResponse() + " " + msg);
            }
        } else {
            String msg = "Answer ID " + answer.getId() + " was marked as wrong.";
            if (query.getResponse() == null || query.getResponse().isBlank()) {
                query.setResponse(msg);
            } else {
                query.setResponse(query.getResponse() + " " + msg);
            }
            // status stays as is
        }
        query.setUpdatedAt(LocalDateTime.now());
        queryRepository.save(query); // update entity in DB [web:185][web:210]
    }


    @Transactional
    public Answer updateAnswerContent(Long id, String content) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found"));

        answer.setContent(content);
        answer.setUpdatedAt(LocalDateTime.now());

        // IMPORTANT: since student updated the answer, clear previous review
        answer.setAccepted(null);           // so buttons show again
        answer.setReviewedByFacultyId(null);
        answer.setReviewedAt(null);

        return answerRepository.save(answer);
    }

}
