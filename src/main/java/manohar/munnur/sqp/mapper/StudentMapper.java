package manohar.munnur.sqp.mapper;

import manohar.munnur.sqp.dto.StudentDto;
import manohar.munnur.sqp.entity.Student;

public class StudentMapper {

    public static StudentDto mapToStudentDto(Student student) {
        return new StudentDto(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getEmail(),
            student.getPhone(),
            student.getGender(),
            student.getBirthDate(),
            student.getCourseOrProgram(),
            student.getYearOrSemester(),
            student.getPassword(),
            student.getStudentImage(),
            student.isVerified(),
            null
        );
    }

    // CORRECT: Use setters OR no-arg constructor + setters
    public static Student mapToStudent(StudentDto studentDto) {
        Student student = new Student(); // ✅ No-arg constructor
        
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setEmail(studentDto.getEmail());
        student.setPhone(studentDto.getPhone());
        student.setGender(studentDto.getGender());
        student.setBirthDate(studentDto.getBirthDate());
        student.setCourseOrProgram(studentDto.getCourseOrProgram());
        student.setYearOrSemester(studentDto.getYearOrSemester());
        student.setPassword(studentDto.getPassword());
        student.setStudentImage(studentDto.getStudentImage());
        student.setVerified(studentDto.isVerified());
        
        // ID = null → Hibernate generates custom StudentIdGenerator ID
        return student;
    }
}
