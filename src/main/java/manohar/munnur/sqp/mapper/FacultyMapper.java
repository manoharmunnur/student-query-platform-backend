package manohar.munnur.sqp.mapper;

import manohar.munnur.sqp.dto.FacultyDto;
import manohar.munnur.sqp.entity.Faculty;

public class FacultyMapper {

    public static FacultyDto mapToFacultyDto(Faculty faculty) {
        return new FacultyDto(
            faculty.getId(),
            faculty.getFirstName(),
            faculty.getLastName(),
            faculty.getEmail(),
            faculty.getPhone(),
            faculty.getGender(),
            faculty.getBirthDate(),
            faculty.getDepartment(),
            faculty.getQualification(),
            faculty.getSubjects(),
            faculty.getExperience(),
            faculty.getPassword(),
            faculty.getFacultyImage(),
            faculty.isVerified(),
            null
        );
    }

    public static Faculty mapToFaculty(FacultyDto dto) {
        return new Faculty(
            dto.getId(),
            dto.getFirstName(),
            dto.getLastName(),
            dto.getEmail(),
            dto.getPhone(),
            dto.getGender(),
            dto.getBirthDate(),
            dto.getDepartment(),
            dto.getQualification(),
            dto.getSubjects(),
            dto.getExperience(),
            dto.getPassword(),
            dto.getFacultyImage(),
            dto.isVerified()
        );
    }
}
