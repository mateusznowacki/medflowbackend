package pl.medflow.medflowbackend.domain.entities.embedded;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank
    private String street;

    private String houseNumber;

    private String apartmentNumber;

    @NotBlank
    @Pattern(regexp = "\\d{2}-\\d{3}", message = "Invalid postal code format (expected: XX-XXX)")
    private String postalCode;

    @NotBlank
    private String city;

    // W polskim kontekście: voivodeship
    private String voivodeship;

    @NotBlank
    private String country;
}
