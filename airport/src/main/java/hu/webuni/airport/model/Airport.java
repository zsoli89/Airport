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
@Cacheable
public class Airport {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include()
    private long id;

    @Size(min = 3, max = 20)
    private String name;
    private String iata;

//    @Fetch()
//    a Fetch kaphat join select subselect-et, selectnél minden betöltődik, join-nak nem lesz hatása mert csak id alapú
//    keresés alapján hat, subselect manytoone teljesen használni
//    ha nem akarjuk a plus N select akkor lazynek kell jelölni a kapcsolatot
//    ha lazy initialization hibát kapunk, valószínű hogy lecsatolt állapotban akarjuk betölteni az entitásokat, amikor a
//    az Airpotban az address getter meghívja akkor dobja a hibát
//    az airportban amik bent vannak tagváltozók az address helyén az nem az address az address proxy, dinamikusan generálja a
//    hibernate, amikor elérik a propertyt akkor fordul db-hez de már le van csatolva
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

//    a kovetkezo mappedBy utani resz (FetchType.EAGER) ritkabban javasolt
//	  @Fetch(FetchMode.JOIN) ez csak queryben működik

//    subselect eager typeal
//    Fetch(FetchMode.SUBSELECT)
//    @OneToMany(mappedBy = "takeoff", fetch = FetchType.EAGER)
//    SUBSELECT se kell mert az is plusz lekerdezes, inkabb a query-ben gondolkodni az osszese adatrol
//    Subselect esetén ha fullba kérem lazy initialization exception-t dob még akkor is ha megadjuk neki az EAGER typo-t
//    nem tölti be a departureket

//    sima eager type
//    @OneToMany(mappedBy = "takeoff", fetch = FetchType.EAGER) és ha repoban EntityGraphfal adom meg annak alapértelmezett .FETCH
//    a típusa és nem töltődik be
//    fetchtype eager esetben hiába nem kérem fullba az össze adatot, a hibernate hívás megtörténik annyi alkalommal
//    ahány Flight van

    @OneToMany(mappedBy = "takeoff")
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
