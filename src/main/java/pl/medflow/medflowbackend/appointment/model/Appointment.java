package pl.medflow.medflowbackend.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.enums.AppointmentStatus;

import java.time.Instant;

@Document(collection = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        // szybkie wyszukiwanie i kontrola kolizji dla lekarza i pacjenta
        @CompoundIndex(name = "doctor_start_idx",  def = "{ 'doctorId': 1, 'startTime': 1 }"),
        @CompoundIndex(name = "patient_start_idx", def = "{ 'patientId': 1, 'startTime': 1 }"),
        // rezerwacja konkretnego pokoju w placówce
        @CompoundIndex(name = "facility_room_start_idx", def = "{ 'facilityId': 1, 'roomNumber': 1, 'startTime': 1 }")
})
public class Appointment {

    @Id
    private String id;

    private String doctorId;
    private String patientId;

    private String facilityId;
    private String roomNumber;

    // specjalizacja użyta przy rezerwacji (cache decyzji routingu)
    private String specialization;

    // NOWE: powiązana procedura (czas trwania z katalogu procedur)
    private String procedureId;

    private Instant startTime;
    private Instant endTime;

    private AppointmentStatus status; // SCHEDULED, COMPLETED, CANCELED

    // NOWE: metadane i notatki (opcjonalne)
    private String notes;
    private String cancellationReason;
    private String createdByUserId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
