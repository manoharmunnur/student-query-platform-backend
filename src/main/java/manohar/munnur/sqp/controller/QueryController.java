package manohar.munnur.sqp.controller;

import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.dto.CreateQueryRequest;
import manohar.munnur.sqp.dto.QueryDto;
import manohar.munnur.sqp.entity.queries.Query;
import manohar.munnur.sqp.mapper.QueryMapper;
import manohar.munnur.sqp.service.QueryService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sqp/queries")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    // POST /api/queries
    @PostMapping
    public ResponseEntity<Query> createQuery(@RequestBody CreateQueryRequest request) {
        Query created = queryService.createQuery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/queries/{id}
    @GetMapping("/{id}")
    public ResponseEntity<QueryDto> getQuery(@PathVariable Long id) {
        return queryService.findById(id)
                .map(QueryMapper::mapToQueryDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<QueryDto>> getAllQueries() {
        List<QueryDto> dtos = queryService.findAll()
                .stream()
                .map(QueryMapper::mapToQueryDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

}
