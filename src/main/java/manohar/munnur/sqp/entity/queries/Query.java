package manohar.munnur.sqp.entity.queries;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "queries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of whoever created (student or faculty)
    @Column(nullable = false)
    private String createdByUserId;

    // "STUDENT" or "FACULTY"
    @Column(nullable = false, length = 20)
    private String createdByRole;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    private QueryStatus status = QueryStatus.PENDING;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] attachment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    @Column(columnDefinition = "TEXT")
    private String response;

    @OneToMany(mappedBy = "query",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("voteCount DESC, createdAt ASC")
    @JsonIgnore
    private java.util.List<Answer> answers = new java.util.ArrayList<>();
}
