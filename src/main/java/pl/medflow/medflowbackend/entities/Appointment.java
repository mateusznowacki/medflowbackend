package pl.medflow.medflowbackend.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.enums.AppointmentStatus;

import java.time.Instant;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    private String id;

    private String doctorId;
    private String patientId;

    private String facilityId;
    private String roomNumber;

    private String specialization;

    private Instant startTime;
    private Instant endTime;

    private AppointmentStatus status;    // np. SCHEDULED, COMPLETED, CANCELED
}
