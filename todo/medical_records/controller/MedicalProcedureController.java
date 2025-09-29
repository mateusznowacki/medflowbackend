package pl.medflow.medflowbackend.todo.medical_records.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalProcedure;
import pl.medflow.medflowbackend.todo.medical_records.service.MedicalProcedureService;

import java.util.List;

@RestController
@RequestMapping("/api/procedures")
@RequiredArgsConstructor
public class MedicalProcedureController {

    private final MedicalProcedureService procedureService;

    @PostMapping
    public ResponseEntity<MedicalProcedure> create(@RequestBody MedicalProcedure proc) {
        return ResponseEntity.ok(procedureService.create(proc));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MedicalProcedure> update(@PathVariable String id, @RequestBody MedicalProcedure proc) {
        return ResponseEntity.ok(procedureService.update(id, proc));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalProcedure> getById(@PathVariable String id) {
        return ResponseEntity.ok(procedureService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MedicalProcedure>> getAll() {
        return ResponseEntity.ok(procedureService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        procedureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
