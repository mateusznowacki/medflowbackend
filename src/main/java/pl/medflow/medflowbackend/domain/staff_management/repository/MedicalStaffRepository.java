package pl.medflow.medflowbackend.domain.staff_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.domain.staff_management.model.MedicalStaff;

public interface MedicalStaffRepository extends MongoRepository<MedicalStaff, String> { }
