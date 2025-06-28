package pl.medflow.medflowbackend.entities.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomLocation {
    private String building;
    private String floor;
    private String roomNumber;
}
