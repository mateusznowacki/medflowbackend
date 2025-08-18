package pl.medflow.medflowbackend.entities.embedded;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomLocation {

    @NotBlank
    private String building;

    private String floor;

    @NotBlank
    private String roomNumber;

}
