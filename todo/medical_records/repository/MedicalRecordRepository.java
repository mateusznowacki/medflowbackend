package pl.medflow.medflowbackend.todo.medical_records.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalRecord;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientId(String patientId);
    List<MedicalRecord> findByDoctorId(String doctorId);
}
