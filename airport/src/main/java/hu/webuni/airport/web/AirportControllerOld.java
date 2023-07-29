package hu.webuni.airport.web;

import hu.webuni.airport.api.model.AirportDto;
import hu.webuni.airport.mapper.AirportMapper;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.HistoryData;
import hu.webuni.airport.repository.AirportRepository;
import hu.webuni.airport.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
//@RestController
@RequestMapping("/api/airports")
public class AirportControllerOld {

    private final AirportService airportService;
    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;


//	@GetMapping
//	public List<AirportDto> getAll(){
//		List<Airport> airports = airportService.findAll();
//		return airportMapper.airportsToDtos(airports);
//	}

    //	@GetMapping
//	public List<AirportDto> getAll(){
//		List<Airport> airports = airportRepository.findAllWithAddress();
//		return airportMapper.airportsToDtos(airports);
//	}
    // akarunk bele addresseket vagy ne
    // ha nem akarjuk a plusz n selectet akkor az Airport Entity osztaly Address ManyToOne kapcsolataba Lazy fetch type kell
    @GetMapping
    public List<AirportDto> getAll(
            @RequestParam Optional<Boolean> full, @SortDefault("id") Pageable pageable
    ) {
//        ha Pageable használunk, akkor a queryben ha nem rendezünk semmi szerint, semmi garancia nincs rá hogy két egymást
//        követő lapozást megfelelően működik, ezért a @SortDefault-tal védekezhetünk
//        enélkül ha lapozunk megjelenhetnek olyan sorok amik az előző oldalon voltak
        boolean isFull = full.orElse(false);
        List<Airport> airports = isFull
//                ? airportRepository.findAllWithAddressAndDeparturesAndLanding() --> N*M sor jon vissza, ha N arrival es M departure van
                ? airportService.findAllWithRelationships(pageable)
                : airportRepository.findAll(pageable).getContent();
        return isFull
                ? airportMapper.airportsToDtos(airports)
                : airportMapper.airportSummariesToDtos(airports)
                ;
//        a lentivel meg lehet nézni mennyivel több lekérdezés ez az egész: 13
//        List<Airport> airports = airportRepository.findAll();
//        return airportMapper.airportsToDtos(airports);

    }

    @GetMapping("/{id}")
    public AirportDto getById(@PathVariable long id) {
        Airport airport = airportService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return airportMapper.airportSummaryToDto(airport);
    }

    @PostMapping
    public AirportDto createAirport(@RequestBody @Valid AirportDto airportDto) {
        Airport airport = airportService.save(airportMapper.dtoToAirport(airportDto));
        return airportMapper.airportToDto(airport);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportDto> modifyAirport(@PathVariable long id, @RequestBody AirportDto airportDto) {
        Airport airport = airportMapper.dtoToAirport(airportDto);
        airport.setId(id);
        try {
            AirportDto savedAirportDto = airportMapper.airportToDto(airportService.update(airport));

            return ResponseEntity.ok(savedAirportDto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteAirport(@PathVariable long id) {
        airportService.delete(id);
    }

    @GetMapping("/{id}/history")
    public List<AirportDto> getHistoryById(@PathVariable long id) {
        List<Airport> airport = airportService.getAirportHistory(id);

        return airportMapper.airportSummariesToDtos(airport);
    }

//    plus revision infokkal
    @GetMapping("/{id}/history2")
    public List<HistoryData<AirportDto>> getHistoryById2(@PathVariable long id) {
        List<HistoryData<Airport>> airportList = airportService.getAirportHistory2(id);

        List<HistoryData<AirportDto>> airportDtosWithHistory = new ArrayList<>();
        airportList.forEach(hd -> {
            airportDtosWithHistory.add(new HistoryData<>(
                    airportMapper.airportSummaryToDto(hd.getData()),
                    hd.getRevType(),
                    hd.getRevision(),
                    hd.getDate()));
        });
        return airportDtosWithHistory;
    }

    @GetMapping("/{id}/history3")
    public List<HistoryData<AirportDto>> getHistoryById3(@PathVariable long id) {
        List<HistoryData<Airport>> airportList = airportService.getAirportHistory3(id);

        List<HistoryData<AirportDto>> airportDtosWithHistory = new ArrayList<>();
        airportList.forEach(hd -> {
            airportDtosWithHistory.add(new HistoryData<>(
                    airportMapper.airportToDto(hd.getData()),
                    hd.getRevType(),
                    hd.getRevision(),
                    hd.getDate()));
        });
        return airportDtosWithHistory;
    }
}
