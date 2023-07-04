package hu.webuni.airport.model;

import lombok.*;
import org.hibernate.envers.RevisionType;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryData<T> {

    private T data;
    private RevisionType revType;
    private int revision;
    private Date date;
}
