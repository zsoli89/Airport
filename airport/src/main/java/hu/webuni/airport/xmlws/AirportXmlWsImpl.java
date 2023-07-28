package hu.webuni.airport.xmlws;

import hu.webuni.airport.api.model.HistoryDataAirportDto;
import hu.webuni.airport.mapper.HistoryDataMapper;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.model.HistoryData;
import hu.webuni.airport.service.AirportService;
import hu.webuni.airport.service.DelayService;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.annotations.UseAsyncMethod;
import org.apache.cxf.jaxws.ServerAsyncResponse;
import org.springframework.stereotype.Service;

import javax.xml.ws.AsyncHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Service
public class AirportXmlWsImpl implements AirportXmlWs {

    private final AirportService airportService;
    private final HistoryDataMapper historyDataMapper;
    private final DelayService delayService;

    @Override
    public List<HistoryDataAirportDto> getHistoryById3(Long id) {
        List<HistoryData<Airport>> airportList = airportService.getAirportHistory3(id);

        List<HistoryDataAirportDto> airportDtosWithHistory = new ArrayList<>();
        airportList.forEach(hd -> airportDtosWithHistory.add(historyDataMapper.airportHistoryDataToDto(hd)));
        return airportDtosWithHistory;
    }

    @Override
    @UseAsyncMethod // amikor ezt a metodust hivnank meg akkor ne ot hivja meg hanem az async parjat
    public int getFlightDelay(long flightId) {
        return 0;
    }

    public Future<Integer> getFlightDelayAsync(long flightId, AsyncHandler<Integer> asyncHandler) {
//        ez egy cxf-es valasz
        ServerAsyncResponse<Integer> serverAsyncResponse = new ServerAsyncResponse<>();
        System.out.println("Szál: " + Thread.currentThread().getName());

        delayService.getDelayAsync(flightId).thenAccept(result -> {
            System.out.println("Szál: " + Thread.currentThread().getName());
            serverAsyncResponse.set(result);
            asyncHandler.handleResponse(serverAsyncResponse);
        });
        return serverAsyncResponse;
    }

}
