package com.xamarsia.store.service;

import com.xamarsia.store.entity.Category;
import com.xamarsia.store.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> all() {
        return repository.findAll();
    }

    public Category save(@NonNull final Category category) {
        return repository.save(category);
    }

    public Category replace(@NonNull final Long id, @NonNull final Category newCategory) throws Exception {
        Category category = getCategoryById(id);
        category.setTitle(newCategory.getTitle());

        return repository.save(category);
    }

    @Transactional
    public void delete(@NonNull final Long id) {
        repository.deleteById(id);
    }

    public Category getCategoryById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Genre not found with this id: " + id));
    }
}
