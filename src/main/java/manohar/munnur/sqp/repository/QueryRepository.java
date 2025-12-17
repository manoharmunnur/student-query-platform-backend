package manohar.munnur.sqp.repository;

import manohar.munnur.sqp.entity.queries.Query;
import manohar.munnur.sqp.entity.queries.QueryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<Query, Long> {

    // All queries by creator (student or faculty)
    List<Query> findByCreatedByUserId(String createdByUserId);

    // All queries by status (no sorting)
    List<Query> findByStatus(QueryStatus status);

    // Student/faculty own queries with status + sorted by time
    List<Query> findByCreatedByUserIdAndCreatedByRoleAndStatusOrderByCreatedAtDesc(
    		String createdByUserId,
            String createdByRole,
            QueryStatus status
    );

    // All queries by status sorted by time
    List<Query> findByStatusOrderByCreatedAtDesc(QueryStatus status);
}
