package pl.medflow.medflowbackend.domain.staff_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.identity.user.model.User;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;


@Document(collection = "staff")
@TypeAlias("medicalStaff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@CompoundIndex(name = "email_unique_staff", def = "{ 'email': 1 }", unique = true)
public class MedicalStaff extends User {

    private MedicalStaffPosition position;

    /** TODO change to departmentId later */
    private String department;

    private RoomLocation assignedRoom;

    @Indexed(unique = true, sparse = true)
    private String licenseNumber;
}
