package pl.medflow.medflowbackend.entities.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.entities.embedded.RoomLocation;
import pl.medflow.medflowbackend.enums.MedicalStaffPosition;

import java.time.LocalDate;

@Document(collection = "users")
@TypeAlias("medicalStaff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalStaff extends User {

    private MedicalStaffPosition position;
    private String department;
    private RoomLocation assignedRoom;
    private String licenseNumber;



}
