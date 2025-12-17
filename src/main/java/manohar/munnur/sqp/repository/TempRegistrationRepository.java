package manohar.munnur.sqp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import manohar.munnur.sqp.entity.TempRegistration;
import java.util.Optional;

public interface TempRegistrationRepository extends JpaRepository<TempRegistration, String> {
    Optional<TempRegistration> findByEmail(String email);
}
