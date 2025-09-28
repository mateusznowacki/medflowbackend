package pl.medflow.medflowbackend.todo.doctor_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.todo.doctor_management.DoctorResponse;
import pl.medflow.medflowbackend.todo.doctor_management.model.Doctor;
import pl.medflow.medflowbackend.todo.doctor_management.model.DoctorRegistrationRequest;
import pl.medflow.medflowbackend.todo.doctor_management.repository.DoctorRepository;
import pl.medflow.medflowbackend.domain.identity.account.Account;
import pl.medflow.medflowbackend.domain.identity.account.AccountRepository;
import pl.medflow.medflowbackend.todo.medical_records.model.MedicalProcedure;
import pl.medflow.medflowbackend.todo.medical_records.repository.MedicalProcedureRepository;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final AccountRepository accountRepo;
    private final MedicalProcedureRepository procedureRepo;
    private final PasswordEncoder passwordEncoder;

    // CREATE / REGISTER
    public DoctorResponse registerDoctor(DoctorRegistrationRequest req) {
        if (accountRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Doctor with this email already exists");
        }

        List<MedicalProcedure> procedures = resolveProcedures(req.getProcedureIds());

        Doctor doc = Doctor.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                //.password(passwordEncoder.encode(req.getPassword()))
                .phoneNumber(req.getPhoneNumber())
                .role(Role.DOCTOR)
                .licenseNumber(req.getLicenseNumber())
                .specialization(req.getSpecialization())
                .department(req.getDepartment())
                .office(req.getOffice())
                .acceptingPatients(Boolean.TRUE.equals(req.getAcceptingPatients()))
                .procedures(procedures)
                .build();

        Doctor saved = doctorRepo.save(doc);

        // Create login account linked by the same ID
        accountRepo.save(Account.builder()
                .id(saved.getId())
                .email(saved.getEmail())
             //   .passwordHash(saved.getPassword())
                .role(Role.DOCTOR)
                .build());

        return toResponse(saved);
    }

    // READ - all
    public List<DoctorResponse> getAllDoctors() {
        return doctorRepo.findAll().stream().map(this::toResponse).toList();
    }

    // READ - by id
    public DoctorResponse getDoctorById(String id) {
        return doctorRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
    }

    // READ - by specialization
    public List<DoctorResponse> getDoctorsBySpecialization(String specialization) {
        return doctorRepo.findAll().stream()
                .filter(d -> d.getSpecialization() != null && d.getSpecialization().equalsIgnoreCase(specialization))
                .map(this::toResponse)
                .toList();
    }

    // READ - by department
    public List<DoctorResponse> getDoctorsByDepartment(String department) {
        return doctorRepo.findAll().stream()
                .filter(d -> d.getDepartment() != null && d.getDepartment().equalsIgnoreCase(department))
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    public DoctorResponse updateDoctor(String id, DoctorRegistrationRequest req) {
        Doctor existing = doctorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (req.getFirstName() != null) existing.setFirstName(req.getFirstName());
        if (req.getLastName() != null) existing.setLastName(req.getLastName());
        if (req.getPhoneNumber() != null) existing.setPhoneNumber(req.getPhoneNumber());
        if (req.getLicenseNumber() != null) existing.setLicenseNumber(req.getLicenseNumber());
        if (req.getSpecialization() != null) existing.setSpecialization(req.getSpecialization());
        if (req.getDepartment() != null) existing.setDepartment(req.getDepartment());
        if (req.getOffice() != null) existing.setOffice(req.getOffice());
        if (req.getAcceptingPatients() != null) existing.setAcceptingPatients(req.getAcceptingPatients());

        if (req.getProcedureIds() != null) {
            List<MedicalProcedure> procedures = resolveProcedures(req.getProcedureIds());
            existing.setProcedures(procedures);
        }

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            String encoded = passwordEncoder.encode(req.getPassword());
          //  existing.setPassword(encoded);
            // update account password as well
            accountRepo.findById(id).ifPresent(acc -> {
                acc.setPasswordHash(encoded);
                accountRepo.save(acc);
            });
        }

        Doctor saved = doctorRepo.save(existing);
        return toResponse(saved);
    }

    // DELETE
    public void deleteDoctor(String id) {
        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        doctorRepo.delete(doctor);
        accountRepo.deleteById(id);
    }

    private List<MedicalProcedure> resolveProcedures(List<String> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        return procedureRepo.findAllById(ids);
    }

    private DoctorResponse toResponse(Doctor d) {
        List<String> procIds = d.getProcedures() == null ? List.of() : d.getProcedures().stream()
                .map(MedicalProcedure::getId)
                .filter(Objects::nonNull)
                .toList();
        return new DoctorResponse(
                d.getId(),
                d.getFirstName(),
                d.getLastName(),
                d.getEmail(),
                d.getPhoneNumber(),
                d.getLicenseNumber(),
                d.getSpecialization(),
                d.getDepartment(),
                d.getOffice(),
                d.isAcceptingPatients(),
                procIds
        );
    }
}
