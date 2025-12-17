package manohar.munnur.sqp.controller;

import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.dto.CreateAnswerRequest;
import manohar.munnur.sqp.dto.AnswerDto;
import manohar.munnur.sqp.mapper.AnswerMapper;
import manohar.munnur.sqp.entity.queries.Answer;
import manohar.munnur.sqp.service.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sqp/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // POST /api/answers  (create answer)
    @PostMapping
    public ResponseEntity<AnswerDto> createAnswer(@RequestBody CreateAnswerRequest request) {
        Answer created = answerService.createAnswer(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AnswerMapper.mapToAnswerDto(created));
    }

    // GET /api/answers/by-query/{queryId}
    @GetMapping("/by-query/{queryId}")
    public ResponseEntity<List<AnswerDto>> getAnswersByQuery(@PathVariable Long queryId) {
        List<AnswerDto> dtos = answerService.findAnswersByQuery(queryId)
                .stream()
                .map(AnswerMapper::mapToAnswerDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // POST /api/answers/{answerId}/vote?facultyId=FAC001&value=1
    @PostMapping("/{answerId}/vote")
    public ResponseEntity<Void> voteAnswer(
            @PathVariable Long answerId,
            @RequestParam String facultyId,
            @RequestParam int value   // 1 or -1
    ) {
        answerService.voteAnswer(answerId, facultyId, value);
        return ResponseEntity.ok().build();
    }

    // POST /api/answers/{answerId}/review?facultyId=2025F0001&accepted=true
    @PostMapping("/{answerId}/review")
    public ResponseEntity<Void> reviewAnswer(
            @PathVariable Long answerId,
            @RequestParam String facultyId,
            @RequestParam boolean accepted
    ) {
        answerService.reviewAnswer(answerId, facultyId, accepted);
        return ResponseEntity.ok().build();
    }
    
    @PatchMapping("/{answerId}")
    public ResponseEntity<AnswerDto> patchAnswer(
            @PathVariable Long answerId,
            @RequestBody Map<String, String> body
    ) {
        String content = body.get("content");
        Answer updated = answerService.updateAnswerContent(answerId, content);
        return ResponseEntity.ok(AnswerMapper.mapToAnswerDto(updated));
    }


}
