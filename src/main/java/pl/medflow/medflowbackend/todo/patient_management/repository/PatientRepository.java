package pl.medflow.medflowbackend.todo.patient_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.medflow.medflowbackend.todo.patient_management.model.Patient;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    // szukanie pacjenta po emailu
    Optional<Patient> findByEmail(String email);

    // szukanie pacjenta po numerze telefonu
    Optional<Patient> findByPhoneNumber(String phoneNumber);

    // szukanie pacjenta po PESEL/nationalId
    Optional<Patient> findByNationalId(String nationalId);

    // sprawdzenie czy istnieje pacjent z danym emailem
    boolean existsByEmail(String email);

    // sprawdzenie czy istnieje pacjent z danym nationalId
    boolean existsByNationalId(String nationalId);
}