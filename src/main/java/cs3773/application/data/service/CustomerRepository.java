package cs3773.application.data.service;

import cs3773.application.data.entity.Customer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

}