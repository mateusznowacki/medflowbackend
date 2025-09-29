package pl.medflow.medflowbackend.todo.medical_records.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalDocument;
import pl.medflow.medflowbackend.todo.medical_records.repository.MedicalDocumentRepository;
import pl.medflow.medflowbackend.infrastructure.s3.AwsS3Storage;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalDocumentService {

    private final MedicalDocumentRepository documentRepo;
    private final AwsS3Storage storage;

    public MedicalDocument upload(String patientId, String recordId, String type, MultipartFile file, String uploadedBy) throws IOException {
        String key = "patients/" + patientId + "/" + file.getOriginalFilename();

        storage.upload(key, file.getInputStream(), file.getSize(), file.getContentType());

        MedicalDocument doc = MedicalDocument.builder()
                .patientId(patientId)
                .recordId(recordId)
                .type(type)
                .fileKey(key)
                .contentType(file.getContentType())
                .size(file.getSize())
                .uploadedByUserId(uploadedBy)
                .uploadedAt(Instant.now())
                .status("AVAILABLE")
                .build();

        return documentRepo.save(doc);
    }

    public byte[] download(String documentId) throws IOException {
        MedicalDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        try (var in = storage.download(doc.getFileKey())) {
            return in.readAllBytes();
        }
    }

    public List<MedicalDocument> listByPatient(String patientId) {
        return documentRepo.findByPatientId(patientId);
    }

    public void delete(String documentId) {
        MedicalDocument doc = documentRepo.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        storage.delete(doc.getFileKey());
        doc.setStatus("DELETED");
        documentRepo.save(doc);
    }
}
