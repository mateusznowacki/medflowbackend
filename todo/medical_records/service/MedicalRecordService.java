package pl.medflow.medflowbackend.todo.medical_records.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalRecord;
import pl.medflow.medflowbackend.todo.medical_records.repository.MedicalRecordRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;

    public MedicalRecord create(MedicalRecord record) {
        return recordRepo.save(record);
    }

    public MedicalRecord update(String id, MedicalRecord record) {
        MedicalRecord existing = recordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));

        existing.setVisitAt(record.getVisitAt());
        existing.setAppointmentId(record.getAppointmentId());
        existing.setProcedureIds(record.getProcedureIds());
        existing.setDocumentIds(record.getDocumentIds());

        return recordRepo.save(existing);
    }

    public void delete(String id) {
        recordRepo.deleteById(id);
    }

    public MedicalRecord getById(String id) {
        return recordRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
    }

    public List<MedicalRecord> getByPatient(String patientId) {
        return recordRepo.findByPatientId(patientId);
    }

    public List<MedicalRecord> getByDoctor(String doctorId) {
        return recordRepo.findByDoctorId(doctorId);
    }
}
