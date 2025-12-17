package manohar.munnur.sqp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import manohar.munnur.sqp.entity.queries.AnswerVote;

@Repository
public interface AnswerVoteRepository extends JpaRepository<AnswerVote, Long> {

    Optional<AnswerVote> findByAnswerIdAndFacultyId(Long answerId, String facultyId);

    long countByAnswerIdAndValue(Long answerId, int value);
}
