package manohar.munnur.sqp.service;

import manohar.munnur.sqp.dto.FacultyDto;

public interface FacultyService {

    FacultyDto registerFaculty(FacultyDto facultyDto);

    FacultyDto loginFaculty(String email, String password);

    FacultyDto getFacultyById(String id);

    FacultyDto getFacultyByEmail(String email);

    FacultyDto getFacultyByPhone(Long phone);

    FacultyDto updateFaculty(String id, FacultyDto facultyDto);

    void deleteFaculty(String id);

    // temp flow
    String saveTempFaculty(FacultyDto facultyDto); // returns tempId
    
    // OTP methods
    String sendOtp(String tempId);
    String verifyOtp(String tempId, String emailOtp);
}
