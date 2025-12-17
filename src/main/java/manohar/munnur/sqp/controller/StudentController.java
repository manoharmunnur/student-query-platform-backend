package manohar.munnur.sqp.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.dto.LoginRequest;
import manohar.munnur.sqp.dto.OtpVerifyRequest;
import manohar.munnur.sqp.dto.StudentDto;
import manohar.munnur.sqp.service.StudentService;

@RestController
@RequestMapping("/sqp/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    
    private final ObjectMapper mapper = new ObjectMapper();

    // ===================== TEMP REGISTRATION + OTP =====================
    @PostMapping(value = "/register-temp", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> registerTempStudent(
          @Valid @RequestPart("student") String studentJson,
          @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        StudentDto studentDto = mapper.readValue(studentJson, StudentDto.class);

        if (image != null && !image.isEmpty())
            studentDto.setStudentImage(image.getBytes());

        // Save temp registration and return tempId
        String tempId = studentService.saveTempStudent(studentDto);

        // Optionally send OTP immediately
        studentService.sendOtp(tempId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                		"tempId", tempId,
                		"message","Temp registration created. TempId: " + tempId + ". OTP sent to " + studentDto.getEmail()
                	));
    }

    @PostMapping("/send-otp/{tempId}")
    public ResponseEntity<String> sendOtp(@PathVariable String tempId) {
        String response = studentService.sendOtp(tempId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
        String response = studentService.verifyOtp(request.getUserId(), request.getEmailOtp());
        return ResponseEntity.ok(response);
    }


    // LOGIN API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        StudentDto student = studentService.loginStudent(request.getEmail(), request.getPassword());

        if (student == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "INVALID_CREDENTIALS"));
        }

        if (!student.isVerified()) {

        	// Optional: frontend can call /send-otp if needed
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "OTP verification required",
                            "userId", student.getId()
                    ));
        }

        return ResponseEntity.ok(student);
    }


    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getStudentImage(@PathVariable String id) {

        StudentDto student = studentService.getStudentById(id);

        if (student == null || student.getStudentImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")   // or image/png
                .body(student.getStudentImage());
    }


    @GetMapping("/id/{id}")
    public ResponseEntity<StudentDto> getStudentByUsingId(@PathVariable("id") String studenId){
    	StudentDto studentDto = studentService.getStudentById(studenId);
        if (studentDto == null) return ResponseEntity.notFound().build();
    	return ResponseEntity.ok(studentDto);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<StudentDto> getStudentByUsingEmail(@PathVariable("email") String email){
    	StudentDto studentDto = studentService.getStudentByEmail(email);
        if (studentDto == null) return ResponseEntity.notFound().build();
    	return ResponseEntity.ok(studentDto);
    }
    
    @GetMapping("/phone/{phone}")
    public ResponseEntity<StudentDto> getStudentByUsingPhone(@PathVariable("phone") Long phone){
    	StudentDto studentDto = studentService.getStudentByPhone(phone);
        if (studentDto == null) return ResponseEntity.notFound().build();
    	return ResponseEntity.ok(studentDto);
    }
    
    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<StudentDto> updateStudent(
            @PathVariable("id") String studentId,
            @RequestPart("student") String studentJson,        // JSON as String
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        // Convert JSON string to StudentDto
        StudentDto updatedStudent = mapper.readValue(studentJson, StudentDto.class);

        // Set image if uploaded
        if (image != null && !image.isEmpty()) {
            updatedStudent.setStudentImage(image.getBytes());
        }

        // Save updated student
        StudentDto studentDto = studentService.updateStudent(studentId, updatedStudent);

        return ResponseEntity.ok(studentDto);
    }

    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteExisingStudent(@PathVariable("id") String studenId){
    	studentService.deleteStudent(studenId);
    	return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
    
}
