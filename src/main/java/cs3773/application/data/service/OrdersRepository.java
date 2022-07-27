package cs3773.application.data.service;

import cs3773.application.data.entity.Orders;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, UUID> {

}