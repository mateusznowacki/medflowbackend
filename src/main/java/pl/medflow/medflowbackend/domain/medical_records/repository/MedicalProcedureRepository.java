package pl.medflow.medflowbackend.domain.medical_records.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.medflow.medflowbackend.domain.medical_records.model.MedicalProcedure;

import java.util.Optional;

@Repository
public interface MedicalProcedureRepository extends MongoRepository<MedicalProcedure, String> {
    Optional<MedicalProcedure> findByCode(String code);
}
