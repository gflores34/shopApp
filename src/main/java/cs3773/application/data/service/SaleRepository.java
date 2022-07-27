package cs3773.application.data.service;

import cs3773.application.data.entity.Sale;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

}