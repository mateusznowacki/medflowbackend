package pl.medflow.medflowbackend.domain.staff_management;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MedicalStaffRepository extends MongoRepository<MedicalStaff, String> {
    boolean existsByLicenseNumber(String licenseNumber);

    Optional<MedicalStaff> findByLicenseNumber(String licenseNumber);
}
