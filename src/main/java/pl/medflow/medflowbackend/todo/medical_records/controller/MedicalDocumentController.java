package pl.medflow.medflowbackend.todo.medical_records.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalDocument;
import pl.medflow.medflowbackend.todo.medical_records.service.MedicalDocumentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<MedicalDocument> upload(
            @RequestParam String patientId,
            @RequestParam(required = false) String recordId,
            @RequestParam String type,
            @RequestParam String uploadedBy,
            @RequestParam MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(documentService.upload(patientId, recordId, type, file, uploadedBy));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable String id) throws IOException {
        byte[] content = documentService.download(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + id)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalDocument>> listByPatient(@PathVariable String patientId) {
        return ResponseEntity.ok(documentService.listByPatient(patientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
