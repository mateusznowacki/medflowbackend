package pl.medflow.medflowbackend.domain.medical_records.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.medflow.medflowbackend.domain.medical_records.model.MedicalDocument;

import java.util.List;

@Repository
public interface MedicalDocumentRepository extends MongoRepository<MedicalDocument, String> {
    List<MedicalDocument> findByPatientId(String patientId);
    List<MedicalDocument> findByRecordId(String recordId);
}
