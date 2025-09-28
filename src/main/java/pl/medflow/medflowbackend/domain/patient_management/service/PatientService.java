package pl.medflow.medflowbackend.domain.patient_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.model.Account;
import pl.medflow.medflowbackend.domain.identity.reposiotry.AccountRepository;
import pl.medflow.medflowbackend.domain.patient_management.dto.PatientConsentRequest;
import pl.medflow.medflowbackend.domain.patient_management.dto.PatientCreateRequest;
import pl.medflow.medflowbackend.domain.patient_management.dto.PatientMedicalUpdateRequest;
import pl.medflow.medflowbackend.domain.patient_management.dto.PatientResponse;
import pl.medflow.medflowbackend.domain.patient_management.dto.PatientSummaryResponse;
import pl.medflow.medflowbackend.domain.patient_management.dto.PatientUpdateRequest;
import pl.medflow.medflowbackend.domain.patient_management.model.Patient;
import pl.medflow.medflowbackend.domain.patient_management.repository.PatientRepository;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepo;
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    // === CREATE ===
    public PatientResponse create(PatientCreateRequest req) {
        Patient patient = Patient.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
             //   .password(passwordEncoder.encode(req.password()))
                .phoneNumber(req.phoneNumber())
                .role(Role.PATIENT)
                .active(true)
                .emailVerified(false)
                .nationalId(req.nationalId())
                .gender(req.gender())
                .dateOfBirth(req.dateOfBirth())
                .address(req.address())
                .emergencyContactName(req.emergencyContactName())
                .emergencyContactPhone(req.emergencyContactPhone())
                .consentGiven(Boolean.TRUE.equals(req.consentGiven()))
                .allergies(req.allergies())
                .medications(req.medications())
                .chronicDiseases(req.chronicDiseases())
                .familyDoctorId(req.familyDoctorId())
                .build();

        Patient saved = patientRepo.save(patient);

        // Tworzymy konto logowania
        accountRepo.save(Account.builder()
                .id(saved.getId())
                .email(saved.getEmail())
             //   .passwordHash(saved.getPassword())
                .role(Role.PATIENT)
                .build());

        return toResponse(saved);
    }

    // === UPDATE danych ogólnych ===
    public PatientResponse update(String id, PatientUpdateRequest req) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        if (req.firstName() != null) patient.setFirstName(req.firstName());
        if (req.lastName() != null) patient.setLastName(req.lastName());
        if (req.phoneNumber() != null) patient.setPhoneNumber(req.phoneNumber());
        if (req.nationalId() != null) patient.setNationalId(req.nationalId());
        if (req.gender() != null) patient.setGender(req.gender());
        if (req.dateOfBirth() != null) patient.setDateOfBirth(req.dateOfBirth());
        if (req.address() != null) patient.setAddress(req.address());
        if (req.emergencyContactName() != null) patient.setEmergencyContactName(req.emergencyContactName());
        if (req.emergencyContactPhone() != null) patient.setEmergencyContactPhone(req.emergencyContactPhone());
        if (req.allergies() != null) patient.setAllergies(req.allergies());
        if (req.medications() != null) patient.setMedications(req.medications());
        if (req.chronicDiseases() != null) patient.setChronicDiseases(req.chronicDiseases());
        if (req.familyDoctorId() != null) patient.setFamilyDoctorId(req.familyDoctorId());

        Patient updated = patientRepo.save(patient);
        return toResponse(updated);
    }

    // === UPDATE danych medycznych ===
    public PatientResponse updateMedical(String id, PatientMedicalUpdateRequest req) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        if (req.allergies() != null) patient.setAllergies(req.allergies());
        if (req.medications() != null) patient.setMedications(req.medications());
        if (req.chronicDiseases() != null) patient.setChronicDiseases(req.chronicDiseases());

        Patient updated = patientRepo.save(patient);
        return toResponse(updated);
    }

    // === UPDATE zgód ===
    public PatientResponse updateConsents(String id, PatientConsentRequest req) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        patient.setConsentGiven(req.consentGiven());

        if (req.termsAccepted()) {
            patient.setTermsAcceptedAt(Instant.now());
        }
        if (req.privacyPolicyAccepted()) {
            patient.setPrivacyPolicyAcceptedAt(Instant.now());
        }

        Patient updated = patientRepo.save(patient);
        return toResponse(updated);
    }

    // === GET jeden pacjent ===
    public PatientResponse getById(String id) {
        return patientRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
    }

    // === GET lista pacjentów (lekki DTO) ===
    public List<PatientSummaryResponse> getAllSummaries() {
        return patientRepo.findAll().stream()
                .map(p -> new PatientSummaryResponse(
                        p.getId(),
                        p.getFirstName(),
                        p.getLastName(),
                        p.getEmail(),
                        p.getPhoneNumber()
                ))
                .toList();
    }

    // === helper: mapowanie encji -> DTO ===
    private PatientResponse toResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getEmail(),
                p.getPhoneNumber(),
                p.getNationalId(),
                p.getGender(),
                p.getDateOfBirth(),
                p.getAddress(),
                p.getEmergencyContactName(),
                p.getEmergencyContactPhone(),
                p.isConsentGiven(),
                p.getAllergies(),
                p.getMedications(),
                p.getChronicDiseases(),
                p.getFamilyDoctorId()
        );
    }
}
