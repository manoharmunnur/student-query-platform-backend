package manohar.munnur.sqp.service;

import manohar.munnur.sqp.dto.StudentDto;

public interface StudentService {
	
	StudentDto registerStudent(StudentDto studentDto);
	
	StudentDto loginStudent(String email, String password);
	
	StudentDto getStudentById(String id);
	
	StudentDto getStudentByEmail(String email);
	
	StudentDto getStudentByPhone(Long phone);
	
	StudentDto updateStudent(String id, StudentDto studentDto);
	
	void deleteStudent(String id);
	
	// temp-registration flow
    String saveTempStudent(StudentDto studentDto); // returns tempId

    // OTP methods
    String sendOtp(String tempId);
    String verifyOtp(String tempId, String emailOtp);

}
