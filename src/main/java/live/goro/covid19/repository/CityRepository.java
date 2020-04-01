package live.goro.covid19.repository;

import live.goro.covid19.domain.City;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CityRepository extends ReactiveMongoRepository<City, String> {

}
