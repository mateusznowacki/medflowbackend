package pl.medflow.medflowbackend.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.entities.MedicalProcedure;
import pl.medflow.medflowbackend.entities.embedded.RoomLocation;

import java.util.List;

@Document(collection = "doctors")
@TypeAlias("doctor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
        @CompoundIndex(name = "email_unique_doc", def = "{ 'email': 1 }", unique = true),
        @CompoundIndex(name = "dept_accept_idx", def = "{ 'department': 1, 'acceptingPatients': 1 }")
})
public class Doctor extends User {

    @Indexed(unique = true, sparse = true)
    private String licenseNumber;

    /** consider enum or dictionary id instead of free text */
    private String specialization;

    /** change to departmentId when Department will be a real collection */
    private String department;

    private RoomLocation office;

    private List<MedicalProcedure> procedures;

    @Builder.Default
    private boolean acceptingPatients = true;
}
