package live.goro.covid19.service;

import live.goro.covid19.dto.LocationDTO;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {
    private static final String API = "";
    private final ResourceLoader resourceLoader;
    private WebClient client;

    static {
        System.setProperty("java.net.preferIPv6Addresses", "false");
        System.setProperty("java.net.preferIPv4Addresses", "true");
    }

    @PostConstruct
    public void init() throws IOException {
        log.info("IpV6 support : {}", System.getProperty("java.net.preferIPv6Addresses"));
        client = WebClient.builder()
                .build();
    }

    public Flux<LocationDTO> getCities(InetSocketAddress address) {
        String hostAddress = address.getAddress().getHostAddress();
        log.info("Address passed : {}", hostAddress);
        hostAddress = hostAddress.equals("0:0:0:0:0:0:0:1") ? "185.192.69.45" : hostAddress;
        return
                client.get()
                        .uri("https://ipinfo.io/" + hostAddress)
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange()
                        .flatMapMany(response -> response.bodyToMono(GeoApiData.class)
                                .doOnNext(data -> log.info("GeoApi Data : {}", data))
                                .map(data -> LocationDTO.builder()
                                        .city(data.getCity())
                                        .country(data.getCountry())
                                        .zip(data.getPostal())
                                        .selected(true)
                                        .build())

                        );

    }

}

@Value
@Builder
class GeoApiData {
    String ip;
    String city;
    String country;
    String postal;
}
