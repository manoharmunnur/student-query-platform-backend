package manohar.munnur.sqp.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import manohar.munnur.sqp.dto.FacultyDto;
import manohar.munnur.sqp.entity.Faculty;
import manohar.munnur.sqp.entity.OtpVerification;
import manohar.munnur.sqp.entity.TempRegistration;
import manohar.munnur.sqp.exception.*;
import manohar.munnur.sqp.mapper.FacultyMapper;
import manohar.munnur.sqp.repository.FacultyRepository;
import manohar.munnur.sqp.repository.OtpRepository;
import manohar.munnur.sqp.repository.TempRegistrationRepository;
import manohar.munnur.sqp.service.EmailService;
import manohar.munnur.sqp.service.FacultyService;
import manohar.munnur.sqp.util.OtpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class FacultyServiceImpl implements FacultyService {

    private final FacultyRepository facultyRepository;
    private final TempRegistrationRepository tempRegistrationRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final ObjectMapper mapper = new ObjectMapper();

    // ---------- existing DB save (kept) ----------
    @Override
    public FacultyDto registerFaculty(FacultyDto facultyDto) {

        String email = facultyDto.getEmail().trim();
        Long phone = facultyDto.getPhone();

        if (facultyRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException("Email " + email + " already exists");
        }
        if (facultyRepository.existsByPhone(phone)) {
            throw new PhoneNoAlreadyExistException("Phone " + phone + " already exists");
        }

        Faculty faculty = FacultyMapper.mapToFaculty(facultyDto);
        Faculty savedFaculty = facultyRepository.save(faculty);

        return FacultyMapper.mapToFacultyDto(savedFaculty);
    }

    // ---------- save temp (new) ----------
    @Override
    public String saveTempFaculty(FacultyDto facultyDto) {
        String tempId = TempRegistration.generateId();

        TempRegistration temp = TempRegistration.builder()
                .id(tempId)
                .userType("FACULTY")
                .email(facultyDto.getEmail().trim())
                .jsonData(convertDtoToJson(facultyDto))
                .image(facultyDto.getFacultyImage())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        tempRegistrationRepository.save(temp);
        return tempId;
    }

    private String convertDtoToJson(FacultyDto dto) {
        try {
            return mapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Unable to serialize temp registration data", e);
        }
    }

    private FacultyDto convertJsonToDto(String json) {
        try {
            return mapper.readValue(json, FacultyDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse stored temp registration", e);
        }
    }

    // ---------- send OTP (new) ----------
    @Override
    public String sendOtp(String tempId) {
        TempRegistration temp = tempRegistrationRepository.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Temp registration not found"));

        String emailOtp = OtpUtil.generateOtp();

        otpRepository.findByUserId(tempId).ifPresent(otpRepository::delete);

        OtpVerification otp = OtpVerification.builder()
                .userId(tempId)
                .emailOtp(emailOtp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        otpRepository.save(otp);

        emailService.sendOtpEmail(temp.getEmail(), emailOtp);

        return "OTP sent successfully to " + temp.getEmail();
    }

    // ---------- verify and create final faculty (new) ----------
    @Override
    @Transactional
    public String verifyOtp(String tempId, String emailOtp) {
        OtpVerification otp = otpRepository.findByUserId(tempId)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        if (!otp.getEmailOtp().equals(emailOtp)) {
            throw new RuntimeException("Incorrect Email OTP");
        }

        TempRegistration temp = tempRegistrationRepository.findById(tempId)
                .orElseThrow(() -> new RuntimeException("Temp registration not found"));

        if (temp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Temp registration expired");
        }

        FacultyDto dto = convertJsonToDto(temp.getJsonData());
        dto.setFacultyImage(temp.getImage());

        // Check duplicate email/phone once again
        if (facultyRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistException("Email " + dto.getEmail() + " already exists");
        }
        if (facultyRepository.existsByPhone(dto.getPhone())) {
            throw new PhoneNoAlreadyExistException("Phone " + dto.getPhone() + " already exists");
        }

        // Create final faculty and mark verified
        Faculty faculty = FacultyMapper.mapToFaculty(dto);
        faculty.setVerified(true);

        Faculty saved = facultyRepository.save(faculty);

        otpRepository.delete(otp);
        tempRegistrationRepository.delete(temp);

        return "OTP verified successfully! Faculty registration completed. ID: " + saved.getId();
    }

    // ---------------- existing methods (login/get/update/delete) kept as before ----------------
    @Override
    public FacultyDto loginFaculty(String email, String password) {
        Faculty faculty = facultyRepository.findByEmail(email).orElse(null);

        if (faculty == null) {
            return null;
        }
        if (!faculty.getPassword().equals(password)) {
            return null;
        }
        FacultyDto dto = FacultyMapper.mapToFacultyDto(faculty);
        dto.setVerified(faculty.isVerified());
        return dto;
    }

    @Override
    public FacultyDto getFacultyById(String id) {
        return FacultyMapper.mapToFacultyDto(
                facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException("Faculty not found")));
    }

    @Override
    public FacultyDto getFacultyByEmail(String email) {
        return FacultyMapper.mapToFacultyDto(
                facultyRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException("Faculty not found")));
    }

    @Override
    public FacultyDto getFacultyByPhone(Long phone) {
        return FacultyMapper.mapToFacultyDto(
                facultyRepository.findByPhone(phone).orElseThrow(() -> new PhoneNotFoundException("Faculty not found")));
    }

    @Override
    public FacultyDto updateFaculty(String id, FacultyDto updatedFaculty) {
        Faculty faculty = facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException("Faculty not found"));
        // set fields...
        faculty.setFirstName(updatedFaculty.getFirstName());
        faculty.setLastName(updatedFaculty.getLastName());
        faculty.setEmail(updatedFaculty.getEmail());
        faculty.setPhone(updatedFaculty.getPhone());
        faculty.setGender(updatedFaculty.getGender());
        faculty.setBirthDate(updatedFaculty.getBirthDate());
        faculty.setDepartment(updatedFaculty.getDepartment());
        faculty.setQualification(updatedFaculty.getQualification());
        faculty.setSubjects(updatedFaculty.getSubjects());
        faculty.setExperience(updatedFaculty.getExperience());
        faculty.setPassword(updatedFaculty.getPassword());
        faculty.setFacultyImage(updatedFaculty.getFacultyImage());
        Faculty saved = facultyRepository.save(faculty);
        return FacultyMapper.mapToFacultyDto(saved);
    }

    @Override
    public void deleteFaculty(String id) {
        facultyRepository.findById(id).orElseThrow(() -> new FacultyNotFoundException("Faculty not found"));
        facultyRepository.deleteById(id);
    }
}
