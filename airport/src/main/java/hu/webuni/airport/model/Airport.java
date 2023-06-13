package hu.webuni.airport.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Airport {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include()
    private long id;

    @Size(min = 3, max = 20)
    private String name;
    private String iata;
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    //	@OneToMany(mappedBy = "takeoff")
    // a kovetkezo mappedBy utani resz (FetchType.EAGER) ritkabban javasolt
    //	@Fetch(FetchMode.JOIN)
    // SUBSELECT se kell mert az is plusz lekerdezes, inkabb a query-ben gondolkodni az osszese adatrol
    @OneToMany(mappedBy = "takeoff"/*, fetch = FetchType.EAGER*/)
//    @Fetch(FetchMode.SUBSELECT)
    private List<Flight> departures;
    // joinos fetch csak queryba irva mukodik

    // ne List legyen hanem Set, kulonben cannot simultaneously fetch multiple bags hibat kapunk
    // olyan implementaciot tesz moge a hibernate amit mar meg fog enni, egy listat hajlando fetch joinolni
    // de a Set miatt lehet descartes szorzat problema, hiaba egy select lesz a vegeredmeny, 100x100 eredmeny utan 10.000 rekordunk lesz
    // engedjuk el h a selectek szamat minimalizaljuk, inkabb legyen kevesebb sor a visszajovo adatok szama
    @OneToMany(mappedBy = "landing")
    private Set<Flight> arrivals;

    public Airport(String name, String iata) {
        this.name = name;
        this.iata = iata;
    }
}
