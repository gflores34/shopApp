package cs3773.application.data.service;

import cs3773.application.data.entity.Orders;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

    private final OrdersRepository repository;

    @Autowired
    public OrdersService(OrdersRepository repository) {
        this.repository = repository;
    }

    public Optional<Orders> get(UUID id) {
        return repository.findById(id);
    }

    public Orders update(Orders entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Orders> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
