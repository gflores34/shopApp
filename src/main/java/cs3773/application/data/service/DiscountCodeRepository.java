package cs3773.application.data.service;

import cs3773.application.data.entity.DiscountCode;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, UUID> {

}