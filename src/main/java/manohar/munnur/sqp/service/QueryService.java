package manohar.munnur.sqp.service;

import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.dto.CreateQueryRequest;
import manohar.munnur.sqp.entity.queries.Priority;
import manohar.munnur.sqp.entity.queries.Query;
import manohar.munnur.sqp.entity.queries.QueryStatus;
import manohar.munnur.sqp.repository.QueryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueryService {

    private final QueryRepository queryRepository;

    // 1. Create new query (student or faculty)
    public Query createQuery(CreateQueryRequest req) {

        if (req.getCreatedByUserId() == null || req.getCreatedByRole() == null) {
            throw new IllegalArgumentException("createdByUserId and createdByRole are required");
        }

        Priority priority = Priority.MEDIUM;
        if (req.getPriority() != null) {
            priority = Priority.valueOf(req.getPriority().toUpperCase());
        }

        Query query = Query.builder()
                .createdByUserId(req.getCreatedByUserId())
                .createdByRole(req.getCreatedByRole().toUpperCase()) // "STUDENT"/"FACULTY"
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(priority)
                .status(QueryStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return queryRepository.save(query);   // save = insert new entity [web:170]
    }

    // 2. Get single query by id
    public Optional<Query> findById(Long id) {
        return queryRepository.findById(id);   // standard JpaRepository method [web:170]
    }

    // 3. Get all queries
    public List<Query> findAll() {
        return queryRepository.findAll();
    }

    // 4. Get all queries created by a specific user (student or faculty)
    public List<Query> findByCreatedByUser(String userId) {
        return queryRepository.findByCreatedByUserId(userId);
    }

    // 5. Get all queries by status (e.g. PENDING, IN_PROGRESS)
    public List<Query> findByStatus(QueryStatus status) {
        return queryRepository.findByStatus(status);
    }

    // 6. Assign faculty to a query
//    public Query assignFaculty(Long queryId, String facultyId) {
//        Query query = queryRepository.findById(queryId)
//                .orElseThrow(() -> new IllegalArgumentException("Query not found"));
//        query.setAssignedFacultyId(facultyId);
//        query.setStatus(QueryStatus.IN_PROGRESS);
//        query.setUpdatedAt(LocalDateTime.now());
//        return queryRepository.save(query);   // save = update existing entity [web:185][web:170]
//    }

    // 7. Update query status (e.g. mark RESOLVED / CLOSED)
    public Query updateStatus(Long queryId, QueryStatus status) {
        Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new IllegalArgumentException("Query not found"));
        query.setStatus(status);
        if (status == QueryStatus.RESOLVED || status == QueryStatus.CLOSED) {
            query.setResolvedAt(LocalDateTime.now());
        }
        query.setUpdatedAt(LocalDateTime.now());
        return queryRepository.save(query);
    }

    // 8. Add / update faculty response text
    public Query updateResponse(Long queryId, String response) {
        Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new IllegalArgumentException("Query not found"));
        query.setResponse(response);
        query.setUpdatedAt(LocalDateTime.now());
        return queryRepository.save(query);
    }
}
