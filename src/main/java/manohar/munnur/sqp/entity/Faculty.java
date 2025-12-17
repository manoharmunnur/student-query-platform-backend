package manohar.munnur.sqp.entity;

import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "faculties")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Faculty {

    @Id
    @GeneratedValue(generator = "faculty_id")
    @GenericGenerator(
        name = "faculty_id",
        strategy = "manohar.munnur.sqp.idgenerator.FacultyIdGenerator"
    )
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull(message = "Phone cannot be null")
    @Min(1000000000L)
    @Max(9999999999L)
    @Column(unique = true, nullable = false)
    private Long phone;

    private String gender;
    private String birthDate;
    private String department;
    private String qualification;

    // <-- changed to EAGER to avoid LazyInitializationException when serializing DTO
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> subjects;

    private Integer experience;

    @NotNull
    private String password;

    @Lob
    private byte[] facultyImage;

    private boolean verified;
}
