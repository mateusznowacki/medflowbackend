package pl.medflow.medflowbackend.domain.staff_management;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;
import pl.medflow.medflowbackend.domain.shared.user.User;


@Document(collection = "staff")
@TypeAlias("medicalStaff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MedicalStaff extends User {

    private MedicalStaffPosition position;

    private String department;

    private RoomLocation assignedRoom;

    @Indexed(unique = true, sparse = true)
    private String licenseNumber;
}
