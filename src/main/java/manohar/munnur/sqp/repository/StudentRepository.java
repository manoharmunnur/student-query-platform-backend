package manohar.munnur.sqp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import manohar.munnur.sqp.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {
	
	boolean existsByEmail(String email);

    boolean existsByPhone(Long phone);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByPhone(Long phone);
    
}
