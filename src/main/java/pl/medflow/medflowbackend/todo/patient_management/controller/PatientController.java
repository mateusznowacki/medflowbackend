package pl.medflow.medflowbackend.todo.patient_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.patient_management.dto.*;
import pl.medflow.medflowbackend.todo.patient_management.dto.PatientConsentRequest;
import pl.medflow.medflowbackend.todo.patient_management.dto.PatientCreateRequest;
import pl.medflow.medflowbackend.todo.patient_management.dto.PatientMedicalUpdateRequest;
import pl.medflow.medflowbackend.todo.patient_management.dto.PatientResponse;
import pl.medflow.medflowbackend.todo.patient_management.dto.PatientSummaryResponse;
import pl.medflow.medflowbackend.todo.patient_management.dto.PatientUpdateRequest;
import pl.medflow.medflowbackend.todo.patient_management.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // === CREATE pacjent ===
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody PatientCreateRequest request) {
        return ResponseEntity.ok(patientService.create(request));
    }

    // === UPDATE danych ogólnych ===
    @PatchMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientUpdateRequest request
    ) {
        return ResponseEntity.ok(patientService.update(id, request));
    }

    // === UPDATE danych medycznych ===
    @PatchMapping("/{id}/medical")
    public ResponseEntity<PatientResponse> updatePatientMedicalData(
            @PathVariable String id,
            @Valid @RequestBody PatientMedicalUpdateRequest request
    ) {
        return ResponseEntity.ok(patientService.updateMedical(id, request));
    }

    // === UPDATE zgód ===
    @PostMapping("/{id}/consents")
    public ResponseEntity<PatientResponse> updatePatientConsents(
            @PathVariable String id,
            @Valid @RequestBody PatientConsentRequest request
    ) {
        return ResponseEntity.ok(patientService.updateConsents(id, request));
    }

    // === GET jeden pacjent ===
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable String id) {
        return ResponseEntity.ok(patientService.getById(id));
    }

    // === GET lista pacjentów (summary) ===
    @GetMapping
    public ResponseEntity<List<PatientSummaryResponse>> getAllPatientSummaries() {
        return ResponseEntity.ok(patientService.getAllSummaries());
    }
}
