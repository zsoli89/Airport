package hu.webuni.airport.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

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
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] data;
}
