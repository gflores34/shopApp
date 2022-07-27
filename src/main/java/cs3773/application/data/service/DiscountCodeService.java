package cs3773.application.data.service;

import cs3773.application.data.entity.DiscountCode;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DiscountCodeService {

    private final DiscountCodeRepository repository;

    @Autowired
    public DiscountCodeService(DiscountCodeRepository repository) {
        this.repository = repository;
    }

    public Optional<DiscountCode> get(UUID id) {
        return repository.findById(id);
    }

    public DiscountCode update(DiscountCode entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<DiscountCode> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
