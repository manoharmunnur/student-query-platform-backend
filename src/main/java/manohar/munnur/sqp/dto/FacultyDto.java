package manohar.munnur.sqp.dto;

import java.util.List;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyDto {

    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Phone number cannot be null")
    @Min(1000000000L)
    @Max(9999999999L)
    private Long phone;

    private String gender;
    private String birthDate;
    private String department;
    private String qualification;
    private List<String> subjects;
    private Integer experience;

    @NotNull(message = "Password cannot be null")
    private String password;

    private byte[] facultyImage;

    private boolean verified;

    // optional message field for responses
    private String message;
}
