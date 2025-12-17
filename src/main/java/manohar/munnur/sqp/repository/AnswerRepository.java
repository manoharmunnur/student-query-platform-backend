package manohar.munnur.sqp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import manohar.munnur.sqp.entity.queries.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // list answers of a query sorted by votes
    List<Answer> findByQueryIdOrderByVoteCountDescCreatedAtAsc(Long queryId);
}
