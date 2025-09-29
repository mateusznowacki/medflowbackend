// domain/doctor_management/repository/DoctorRepository.java
package pl.medflow.medflowbackend.todo.doctor_management.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.todo.doctor_management.model.Doctor;

public interface DoctorRepository extends MongoRepository<Doctor, String> { }
