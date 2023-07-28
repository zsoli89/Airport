package hu.webuni.airport.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class DelayMessage {

    private int delay;
    private OffsetDateTime timestamp;
}
