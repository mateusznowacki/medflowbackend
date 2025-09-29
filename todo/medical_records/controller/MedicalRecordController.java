package pl.medflow.medflowbackend.todo.medical_records.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalRecord;
import pl.medflow.medflowbackend.todo.medical_records.service.MedicalRecordService;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    @PostMapping
    public ResponseEntity<MedicalRecord> create(@RequestBody MedicalRecord record) {
        return ResponseEntity.ok(recordService.create(record));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MedicalRecord> update(@PathVariable String id, @RequestBody MedicalRecord record) {
        return ResponseEntity.ok(recordService.update(id, record));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getById(@PathVariable String id) {
        return ResponseEntity.ok(recordService.getById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(recordService.getByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecord>> getByDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(recordService.getByDoctor(doctorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
