package pl.medflow.medflowbackend.entities.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;
    private String houseNumber;
    private String apartamentNumer;
    private String postalCode;
    private String city;
    private String voivodeShip;
    private String country;
}