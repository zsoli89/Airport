package hu.webuni.airport.web;

import hu.webuni.airport.api.AirportControllerApi;
import hu.webuni.airport.api.model.AirportDto;
import hu.webuni.airport.api.model.HistoryDataAirportDto;
import hu.webuni.airport.mapper.AirportMapper;
import hu.webuni.airport.mapper.HistoryDataMapper;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.HistoryData;
import hu.webuni.airport.model.Image;
import hu.webuni.airport.repository.AirportRepository;
import hu.webuni.airport.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AirportConroller implements AirportControllerApi {

    private final NativeWebRequest nativeWebRequest;
    private final AirportService airportService;
    private final AirportMapper airportMapper;
    private final HistoryDataMapper historyDataMapper;
    //    ez kell ahhoz, hogy Pageable-t at tudjunk adni, mivel mar Integer a page a size es String a sort
    private final PageableHandlerMethodArgumentResolver pageableResolver;
    private final AirportRepository airportRepository;

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(nativeWebRequest);
    }

    public void configPageable(@SortDefault("id") Pageable pageable) {}

    public Pageable createPageable(String pageableConfigurerMethodName) {
        Method method;
        try {
            method = this.getClass().getMethod(pageableConfigurerMethodName, Pageable.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        MethodParameter methodParameter = new MethodParameter(method, 0);
//        ez azert kell, mert azt mondja meg a Pageable tipusu metodus argumentum melyik controller metodusnak hanyadik argumentuma
        ModelAndViewContainer mavContainer = null;
        WebDataBinderFactory binderFactory = null;
        Pageable pageable = pageableResolver.resolveArgument(methodParameter, mavContainer, nativeWebRequest, binderFactory);
        return pageable;
    }

    @Override
    public ResponseEntity<List<AirportDto>> getAll(Boolean full, Integer page, Integer size, List<String> sort) {
        boolean isFull = full == null ? false : full;
//      a resolveArgument var egy MethodParameter, mavContainer, nativeWebRequest, binderFactory-t
//        ahhoz hogy kinyerje az inforkat az Argument handler ki kell nyerni, de nem kell hozza mind
        Pageable pageable = createPageable("configPageable");
        List<Airport> airports = isFull
                ? airportService.findAllWithRelationships(pageable)
                : airportRepository.findAll(pageable).getContent();
        List<AirportDto> resultList = isFull
                ? airportMapper.airportsToDtos(airports)
                : airportMapper.airportSummariesToDtos(airports);
        return ResponseEntity.ok(resultList);
    }

    @Override
    public ResponseEntity<AirportDto> createAirport(AirportDto airportDto) {
        Airport airport = airportService.save(airportMapper.dtoToAirport(airportDto));
        return ResponseEntity.ok(airportMapper.airportToDto(airport));
    }

    @Override
    public ResponseEntity<Void> deleteAirport(Long id) {
        airportService.delete(id);
        return ResponseEntity.ok().build();
    }

//    @Override
//    public ResponseEntity<List<AirportDto>> getAll(Pageable pageable, Boolean full) {
//        return AirportControllerApi.super.getAll(pageable, full);
//    }

    @Override
    public ResponseEntity<AirportDto> getById(Long id) {
        Airport airport = airportService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(airportMapper.airportSummaryToDto(airport));
    }

    @Override
    public ResponseEntity<List<AirportDto>> getHistoryById(Long id) {
        List<Airport> airport = airportService.getAirportHistory(id);

        return ResponseEntity.ok(airportMapper.airportSummariesToDtos(airport));
    }

    @Override
    public ResponseEntity<List<HistoryDataAirportDto>> getHistoryById2(Long id) {
        List<HistoryData<Airport>> airportList = airportService.getAirportHistory2(id);

        List<HistoryDataAirportDto> airportDtosWithHistory = new ArrayList<>();
        airportList.forEach(hd -> {
            airportDtosWithHistory.add(historyDataMapper.airportHistoryDataToDto(hd));
        });
        return ResponseEntity.ok(airportDtosWithHistory);
    }

    @Override
    public ResponseEntity<List<HistoryDataAirportDto>> getHistoryById3(Long id) {
        List<HistoryData<Airport>> airportList = airportService.getAirportHistory3(id);

        List<HistoryDataAirportDto> airportDtosWithHistory = new ArrayList<>();
        airportList.forEach(hd -> {
            airportDtosWithHistory.add(historyDataMapper.airportHistoryDataToDto(hd));
        });
        return ResponseEntity.ok(airportDtosWithHistory);
    }

    @Override
    public ResponseEntity<AirportDto> modifyAirport(Long id, AirportDto airportDto) {
        Airport airport = airportMapper.dtoToAirport(airportDto);
        airport.setId(id);
        try {
            AirportDto savedAirportDto = airportMapper.airportToDto(airportService.update(airport));

            return ResponseEntity.ok(savedAirportDto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> uploadImageForAirport(Long id, String fileName, MultipartFile content) {
        Image image = null;
        try {
            image = airportService.saveImageForAirport(id, fileName, content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("/api/images/" + image.getId());
    }
}
