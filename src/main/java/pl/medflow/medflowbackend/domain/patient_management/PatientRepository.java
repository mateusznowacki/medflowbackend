// domain/patient_management/repository/PatientRepository.java
package pl.medflow.medflowbackend.domain.patient_management;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.domain.patient_management.model.Patient;

public interface PatientRepository extends MongoRepository<Patient, String> { }
