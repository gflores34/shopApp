package cs3773.application.data.service;

import cs3773.application.data.entity.Item;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository repository;

    @Autowired
    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Optional<Item> get(UUID id) {
        return repository.findById(id);
    }

    public Item update(Item entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Item> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
