package hu.webuni.airport.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {

    private long id;
    private String country;
    private String city;
    private String street;
    private String zip;
}
