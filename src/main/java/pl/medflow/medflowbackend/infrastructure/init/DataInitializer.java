package pl.medflow.medflowbackend.infrastructure.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.medflow.medflowbackend.domain.doctor_management.model.Doctor;
import pl.medflow.medflowbackend.domain.doctor_management.repository.DoctorRepository;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;
import pl.medflow.medflowbackend.domain.identity.auth.repository.AccountRepository;
import pl.medflow.medflowbackend.domain.patient_management.PatientRepository;
import pl.medflow.medflowbackend.domain.patient_management.model.Patient;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.MedicalStaffRepository;
import pl.medflow.medflowbackend.domain.staff_management.model.MedicalStaff;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final AccountRepository accountRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final MedicalStaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initTestUsers() {
        return args -> {
            createPatient("patient@test.com", "test123", "Jan", "Kowalski");
            createDoctor("doctor@test.com", "test123", "Anna", "Nowak", "Cardiology");
            createStaff("staff@test.com", "test123", "Piotr", "Wiśniewski");
        };
    }

    private void createPatient(String email, String rawPassword, String firstName, String lastName) {
        if (accountRepo.findByEmail(email).isEmpty()) {
            String id = UUID.randomUUID().toString();

            // Account
            Account acc = Account.builder()
                    .id(id)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .role(Role.PATIENT)
                    .build();
            accountRepo.save(acc);

            // Patient
            Patient patient = Patient.builder()
                    .id(id)
                    .role(Role.PATIENT)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .consentGiven(true)
                    .active(true)
                    .build();
            patientRepo.save(patient);

            System.out.println("✅ Created test PATIENT: " + email + " / " + rawPassword);
        }
    }

    private void createDoctor(String email, String rawPassword, String firstName, String lastName, String specialization) {
        if (accountRepo.findByEmail(email).isEmpty()) {
            String id = UUID.randomUUID().toString();

            Account acc = Account.builder()
                    .id(id)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .role(Role.DOCTOR)
                    .build();
            accountRepo.save(acc);

            Doctor doctor = Doctor.builder()
                    .id(id)
                    .role(Role.DOCTOR)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .specialization(specialization)
                    .acceptingPatients(true)
                    .build();
            doctorRepo.save(doctor);

            System.out.println("✅ Created test DOCTOR: " + email + " / " + rawPassword);
        }
    }

    private void createStaff(String email, String rawPassword, String firstName, String lastName) {
        if (accountRepo.findByEmail(email).isEmpty()) {
            String id = UUID.randomUUID().toString();

            Account acc = Account.builder()
                    .id(id)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .role(Role.MEDICAL_STAFF)
                    .build();
            accountRepo.save(acc);

            MedicalStaff staff = MedicalStaff.builder()
                    .id(id)
                    .role(Role.MEDICAL_STAFF)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .build();
            staffRepo.save(staff);

            System.out.println("✅ Created test STAFF: " + email + " / " + rawPassword);
        }
    }
}
