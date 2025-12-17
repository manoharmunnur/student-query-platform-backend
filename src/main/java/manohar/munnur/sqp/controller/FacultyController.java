//package manohar.munnur.sqp.controller;
//
//import java.io.IOException;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import manohar.munnur.sqp.dto.FacultyDto;
//import manohar.munnur.sqp.dto.LoginRequest;
//import manohar.munnur.sqp.dto.OtpVerifyRequest;
//import manohar.munnur.sqp.service.FacultyService;
//
//@RestController
//@RequestMapping("/sqp/faculty")
//@RequiredArgsConstructor
//public class FacultyController {
//
//    private final FacultyService facultyService;
//
//    // Register (no OTP generation here)
//    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
//    public ResponseEntity<?> registerNewStudent(
//            @Valid @RequestPart("faculty") String facultyJson,
//            @RequestPart(value = "image", required = false) MultipartFile image
//    ) throws IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//        FacultyDto facultyDto = mapper.readValue(facultyJson, FacultyDto.class);
//
//        if (image != null && !image.isEmpty()) {
//            facultyDto.setFacultyImage(image.getBytes());
//        }
//
//        String tempId = facultyService.saveTempFaculty(facultyDto);
//        
//        facultyService.sendOtp(tempId);
//        
//        return ResponseEntity.status(HttpStatus.CREATED)
//        		.body("Temp registration created. TempId: " + tempId + ". OTP sent to " + facultyDto.getEmail());
//    }
//    
//    @PostMapping("/send-otp/{tempId}")
//    public ResponseEntity<String> sendOtp(@PathVariable String tempId) {
//        String response = facultyService.sendOtp(tempId);
//        return ResponseEntity.ok(response);
//    }
//
//    // Verify OTP
//    @PostMapping("/verify-otp")
//    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
//        String response = facultyService.verifyOtp(request.getUserId(), request.getEmailOtp());
//        return ResponseEntity.ok(response);
//    }
//    
//    // LOGIN API
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//
//        FacultyDto faculty = facultyService.loginFaculty(request.getEmail(), request.getPassword());
//
//        if (faculty == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("INVALID_CREDENTIALS");
//        }
//
//        if (!faculty.isVerified()) {
//
//            // send OTP here
//            facultyService.sendOtp(faculty.getId());
//
//            // return 200 OK with faculty info
//            return ResponseEntity.ok(faculty);
//        }
//
//        return ResponseEntity.ok(faculty);
//    }
//
//    
//
//    
//    @GetMapping("/{id}/image")
//    public ResponseEntity<byte[]> getFacultyImage(@PathVariable String id) {
//
//        FacultyDto faculty = facultyService.getFacultyById(id);
//
//        if (faculty == null || faculty.getFacultyImage() == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok()
//                .header("Content-Type", "image/jpeg")   // or image/png
//                .body(faculty.getFacultyImage());
//    }
//
//
//    @GetMapping("/id/{id}")
//    public ResponseEntity<FacultyDto> getStudentByUsingId(@PathVariable("id") String facultyId){
//        FacultyDto facultyDto = facultyService.getFacultyById(facultyId);
//        return ResponseEntity.ok(facultyDto);
//    }
//
//    @GetMapping("/email/{email}")
//    public ResponseEntity<FacultyDto> getStudentByUsingEmail(@PathVariable("email") String email){
//        FacultyDto facultyDto = facultyService.getFacultyByEmail(email);
//        return ResponseEntity.ok(facultyDto);
//    }
//
//    @GetMapping("/phone/{phone}")
//    public ResponseEntity<FacultyDto> getStudentByUsingPhone(@PathVariable("phone") Long phone){
//        FacultyDto facultyDto = facultyService.getFacultyByPhone(phone);
//        return ResponseEntity.ok(facultyDto);
//    }
//
//    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
//    public ResponseEntity<FacultyDto> updateExistingFaculty(@PathVariable("id") String facultyId,
//                                                           @RequestPart("faculty") String facultyJson,
//                                                           @RequestPart(value = "image",required = false) MultipartFile image) throws IOException {
//
//        ObjectMapper mapper = new ObjectMapper();
//        FacultyDto facultyDto = mapper.readValue(facultyJson, FacultyDto.class);
//
//        if(image != null && !image.isEmpty())
//            facultyDto.setFacultyImage(image.getBytes());
//
//        FacultyDto updatedFaculty = facultyService.updateFaculty(facultyId, facultyDto);
//        return ResponseEntity.ok(updatedFaculty);
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteExisingFaculty(@PathVariable("id") String facultyId){
//        facultyService.deleteFaculty(facultyId);
//        return ResponseEntity.ok("Account Deleted Successfully");
//    }
//}


package manohar.munnur.sqp.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import manohar.munnur.sqp.dto.FacultyDto;
import manohar.munnur.sqp.dto.LoginRequest;
import manohar.munnur.sqp.dto.OtpVerifyRequest;
import manohar.munnur.sqp.service.FacultyService;

@RestController
@RequestMapping("/sqp/faculty")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    private final ObjectMapper mapper = new ObjectMapper();

    // ===================== TEMP REGISTRATION + OTP =====================
    @PostMapping(value = "/register-temp", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, String>> registerTempFaculty(
            @Valid @RequestPart("faculty") String facultyJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        FacultyDto facultyDto = mapper.readValue(facultyJson, FacultyDto.class);

        if (image != null && !image.isEmpty()) {
            facultyDto.setFacultyImage(image.getBytes());
        }

        // Save temporary faculty and generate tempId
        String tempId = facultyService.saveTempFaculty(facultyDto);
        System.out.println(facultyDto);
        System.out.println(tempId);

        // Send OTP to faculty email
        facultyService.sendOtp(tempId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "tempId", tempId,
                        "message", "Temporary registration created. OTP sent to " + facultyDto.getEmail()
                ));
    }

    // Manually resend OTP (optional)
    @PostMapping("/send-otp/{tempId}")
    public ResponseEntity<String> sendOtp(@PathVariable String tempId) {
        String response = facultyService.sendOtp(tempId);
        return ResponseEntity.ok(response);
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequest request) {
        String response = facultyService.verifyOtp(request.getUserId(), request.getEmailOtp());
        return ResponseEntity.ok(response);
    }

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        FacultyDto faculty = facultyService.loginFaculty(request.getEmail(), request.getPassword());

        if (faculty == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "INVALID_CREDENTIALS"));
        }

        if (!faculty.isVerified()) {
            // Optional: frontend can call /send-otp if needed
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "OTP verification required",
                            "userId", faculty.getId()
                    ));
        }

        return ResponseEntity.ok(faculty);
    }

    // ===================== FETCH FACULTY =====================
    @GetMapping("/id/{id}")
    public ResponseEntity<FacultyDto> getFacultyById(@PathVariable String id) {
        FacultyDto faculty = facultyService.getFacultyById(id);
        if (faculty == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<FacultyDto> getFacultyByEmail(@PathVariable String email) {
        FacultyDto faculty = facultyService.getFacultyByEmail(email);
        if (faculty == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<FacultyDto> getFacultyByPhone(@PathVariable Long phone) {
        FacultyDto faculty = facultyService.getFacultyByPhone(phone);
        if (faculty == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(faculty);
    }

    // ===================== FACULTY IMAGE =====================
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getFacultyImage(@PathVariable String id) {
        FacultyDto faculty = facultyService.getFacultyById(id);
        if (faculty == null || faculty.getFacultyImage() == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg") // or image/png
                .body(faculty.getFacultyImage());
    }

    // ===================== UPDATE FACULTY =====================
    @PutMapping(value = "/update/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<FacultyDto> updateFaculty(
            @PathVariable("id") String id,
            @RequestPart("faculty") String facultyJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        FacultyDto facultyDto = mapper.readValue(facultyJson, FacultyDto.class);

        if (image != null && !image.isEmpty()) {
            facultyDto.setFacultyImage(image.getBytes());
        }

        FacultyDto updatedFaculty = facultyService.updateFaculty(id, facultyDto);
        return ResponseEntity.ok(updatedFaculty);
    }

    // ===================== DELETE FACULTY =====================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteFaculty(@PathVariable String id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}
