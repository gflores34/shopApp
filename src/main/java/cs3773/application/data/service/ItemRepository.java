package cs3773.application.data.service;

import cs3773.application.data.entity.Item;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, UUID> {

}