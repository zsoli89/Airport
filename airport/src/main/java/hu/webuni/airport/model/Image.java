package hu.webuni.airport.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Audited
public class Image {

    @Id
    @GeneratedValue
    private long id;
    private String fileName;
//    @Lob
//    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] data;
}
