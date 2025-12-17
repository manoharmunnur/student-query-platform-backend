package manohar.munnur.sqp.entity;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import manohar.munnur.sqp.idgenerator.StudentIdGenerator;

@Entity
@Table(name = "students")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(generator = "student_id")
    @GenericGenerator(name = "student_id", type = StudentIdGenerator.class) // deprecated in Hibernate 6
    private String id;  // Custom ID (Year + Sequence + 'S')

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotNull(message = "Email cannot be null")
    @Column(unique = true, nullable = false)
    private String email; 

    @NotNull(message = "Phone number cannot be null")
    @Min(value = 1000000000L, message = "Phone number must be 10 digits")
    @Max(value = 9999999999L, message = "Phone number must be 10 digits")
    @Column(unique = true, nullable = false)
    private Long phone;


    private String gender;

    private String birthDate;

    private String courseOrProgram;

    private Integer yearOrSemester;

    @NotNull(message = "Password cannot be null")
    private String password;
    
    @Lob
    private byte[] studentImage;
    
    private boolean verified;
    
}
