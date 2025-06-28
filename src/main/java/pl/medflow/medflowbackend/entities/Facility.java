package pl.medflow.medflowbackend.entities;

import org.springframework.data.annotation.Id;
import pl.medflow.medflowbackend.entities.embedded.RoomLocation;

import java.util.List;

public class Facility {
     @Id
    private String id;

    private String name;
    private String address;
    private List<RoomLocation> rooms;

    private String regionCode;
}
