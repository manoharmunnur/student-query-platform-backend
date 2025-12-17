package manohar.munnur.sqp.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import manohar.munnur.sqp.dto.StudentDto;
import manohar.munnur.sqp.entity.OtpVerification;
import manohar.munnur.sqp.entity.Student;
import manohar.munnur.sqp.entity.TempRegistration;
import manohar.munnur.sqp.exception.EmailAlreadyExistException;
import manohar.munnur.sqp.exception.EmailNotFoundException;
import manohar.munnur.sqp.exception.PhoneNoAlreadyExistException;
import manohar.munnur.sqp.exception.PhoneNotFoundException;
import manohar.munnur.sqp.exception.StudentNotFoundException;
import manohar.munnur.sqp.mapper.StudentMapper;
import manohar.munnur.sqp.repository.OtpRepository;
import manohar.munnur.sqp.repository.StudentRepository;
import manohar.munnur.sqp.repository.TempRegistrationRepository;
import manohar.munnur.sqp.service.EmailService;
import manohar.munnur.sqp.service.StudentService;
import manohar.munnur.sqp.util.OtpUtil;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {
	
	private final StudentRepository studentRepository;
	private final TempRegistrationRepository tempRegistrationRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final ObjectMapper mapper = new ObjectMapper();


	@Override
	public StudentDto registerStudent(StudentDto studentDto) {
		
		System.out.println(studentDto);
		
	    String email = studentDto.getEmail().trim();
	    Long phone = studentDto.getPhone();
	    
		if(studentRepository.existsByEmail(email)) {
			throw new EmailAlreadyExistException("Email "+email+" already exists");
		}
		
		if(studentRepository.existsByPhone(phone)) {
			throw new PhoneNoAlreadyExistException("Phone Number "+phone+" Number already exists");
		}
		
		Student student = StudentMapper.mapToStudent(studentDto);
		Student registeredStudent = studentRepository.save(student);
		
		return StudentMapper.mapToStudentDto(registeredStudent);
	}
	
	@Override
    public String saveTempStudent(StudentDto studentDto) {
        // save DTO JSON + image into TempRegistration
        String tempId = TempRegistration.generateId();

        TempRegistration temp = TempRegistration.builder()
                .id(tempId)
                .userType("STUDENT")
                .email(studentDto.getEmail().trim())
                .jsonData(convertDtoToJson(studentDto))
                .image(studentDto.getStudentImage())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        tempRegistrationRepository.save(temp);
        return tempId;
    }
	
	private String convertDtoToJson(StudentDto dto) {
        try {
            return mapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Unable to serialize temp registration data", e);
        }
    }
	
	private StudentDto convertJsonToDto(String json) {
        try {
            return mapper.readValue(json, StudentDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse stored temp registration", e);
        }
    }

	@Override
    public String sendOtp(String tempId) {

        TempRegistration temp = tempRegistrationRepository.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Temp registration not found"));

        // generate OTP
        String emailOtp = OtpUtil.generateOtp();

        // delete old OTP if exists
        otpRepository.findByUserId(tempId).ifPresent(otpRepository::delete);

        // save new OTP
        OtpVerification otp = OtpVerification.builder()
                .userId(tempId)
                .emailOtp(emailOtp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        otpRepository.save(otp);

        // send OTP email
        emailService.sendOtpEmail(temp.getEmail(), emailOtp);

        return "OTP sent successfully to " + temp.getEmail();
    }

    @Override
    @Transactional
    public String verifyOtp(String tempId, String emailOtp) {

        TempRegistration temp = tempRegistrationRepository.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Student Temp registration not found"));

        OtpVerification otp = otpRepository.findByUserId(temp.getId())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        if (!otp.getEmailOtp().equals(emailOtp)) {
            throw new RuntimeException("Incorrect Email OTP");
        }

        if (temp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Temp registration expired");
        }
        
        // Convert JSON back to DTO
        StudentDto dto = convertJsonToDto(temp.getJsonData());
        dto.setStudentImage(temp.getImage());

        // Check duplicates once again
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistException("Email " + dto.getEmail() + " already exists");
        }
        if (studentRepository.existsByPhone(dto.getPhone())) {
            throw new PhoneNoAlreadyExistException("Phone " + dto.getPhone() + " already exists");
        }

        // Create final Student entity and mark verified
        Student student = StudentMapper.mapToStudent(dto);
        student.setVerified(true);
        
        Student saved = studentRepository.save(student);
        
        // cleanup
        otpRepository.delete(otp);
        tempRegistrationRepository.delete(temp);

        return "OTP verified successfully! Student registration completed. ID: "+saved.getId();
    }

	@Override
	public StudentDto loginStudent(String email, String password) {

        // 1. Check if user exists
        Student student = studentRepository.findByEmail(email)
                .orElse(null);

        if (student == null) {
            return null; // controller will return 401
        }

        // 2. Check password
        if (!student.getPassword().equals(password)) {
            return null;
        }

        // 3. Convert to DTO
        StudentDto dto = StudentMapper.mapToStudentDto(student);

        // 4. If email not verified — return DTO with verified=false
//        if (!student.isVerified()) {
//            dto.setVerified(false);
//            return dto;
//        }

        // 5. If verified — return normal DTO
        dto.setVerified(true);
        return dto;

	}
	


	
	@Override
	public StudentDto getStudentById(String id) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        return StudentMapper.mapToStudentDto(student);
	}

	@Override
	public StudentDto getStudentByEmail(String email) {

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Student not found with email: " + email));

        return StudentMapper.mapToStudentDto(student);
	}

	@Override
	public StudentDto getStudentByPhone(Long phone) {

        Student student = studentRepository.findByPhone(phone)
                .orElseThrow(() -> new PhoneNotFoundException("Student not found with phone: " + phone));

        return StudentMapper.mapToStudentDto(student);
	}
	
	@Override
	public StudentDto updateStudent(String id, StudentDto updatedStudent) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        
        student.setFirstName(updatedStudent.getFirstName());
        student.setLastName(updatedStudent.getLastName());
        student.setEmail(updatedStudent.getEmail());
        student.setPhone(updatedStudent.getPhone());
        student.setGender(updatedStudent.getGender());
        student.setBirthDate(updatedStudent.getBirthDate());
        student.setCourseOrProgram(updatedStudent.getCourseOrProgram());
        student.setPassword(updatedStudent.getPassword());
        student.setStudentImage(updatedStudent.getStudentImage());
        student.setYearOrSemester(updatedStudent.getYearOrSemester());
        
        Student updatedStudentObj = studentRepository.save(student);
        
		return StudentMapper.mapToStudentDto(updatedStudentObj);
	}
	
	@Override
	public void deleteStudent(String id) {
		studentRepository.findById(id)
				.orElseThrow(()->
				new StudentNotFoundException("Student Not Found to delete"));
		studentRepository.deleteById(id);
	}
	
	
}
