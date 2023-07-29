package hu.webuni.airport.repository;

import hu.webuni.airport.model.AirportUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AirportUser, String> {

    Optional<AirportUser> findByFacebookId(String facebookId);
}
