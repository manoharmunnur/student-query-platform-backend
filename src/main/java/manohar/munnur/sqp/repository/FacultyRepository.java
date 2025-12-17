package manohar.munnur.sqp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import manohar.munnur.sqp.entity.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, String> {
    boolean existsByEmail(String email);
    boolean existsByPhone(Long phone);
    Optional<Faculty> findByEmail(String email);
    Optional<Faculty> findByPhone(Long phone);
}
