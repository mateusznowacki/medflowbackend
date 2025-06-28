package pl.medflow.medflowbackend.entities.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.entities.MedicalProcedure;
import pl.medflow.medflowbackend.entities.embedded.RoomLocation;

import java.time.Instant;
import java.util.List;

@Document(collection = "users")
@TypeAlias("doctor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends User {

    private String licenseNumber;

    private String specialization;

    private String department;

    private RoomLocation office;

    private List<MedicalProcedure> procedures;

    @Builder.Default
    private boolean acceptingPatients = true;

}