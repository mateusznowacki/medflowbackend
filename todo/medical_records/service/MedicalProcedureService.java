package pl.medflow.medflowbackend.todo.medical_records.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalProcedure;
import pl.medflow.medflowbackend.todo.medical_records.repository.MedicalProcedureRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalProcedureService {

    private final MedicalProcedureRepository procedureRepo;

    public MedicalProcedure create(MedicalProcedure proc) {
        return procedureRepo.save(proc);
    }

    public MedicalProcedure update(String id, MedicalProcedure proc) {
        MedicalProcedure existing = procedureRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Procedure not found"));

        existing.setName(proc.getName());
        existing.setDescription(proc.getDescription());
        existing.setDurationMinutes(proc.getDurationMinutes());
        existing.setActive(proc.isActive());
        existing.setPrivateVisit(proc.isPrivateVisit());
        existing.setPrice(proc.getPrice());

        return procedureRepo.save(existing);
    }

    public void delete(String id) {
        procedureRepo.deleteById(id);
    }

    public MedicalProcedure getById(String id) {
        return procedureRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Procedure not found"));
    }

    public List<MedicalProcedure> getAll() {
        return procedureRepo.findAll();
    }
}
