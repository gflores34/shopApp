package cs3773.application.data.service;

import cs3773.application.data.entity.Sale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SaleService {

    private final SaleRepository repository;

    @Autowired
    public SaleService(SaleRepository repository) {
        this.repository = repository;
    }

    public Optional<Sale> get(UUID id) {
        return repository.findById(id);
    }

    public Sale update(Sale entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Sale> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
