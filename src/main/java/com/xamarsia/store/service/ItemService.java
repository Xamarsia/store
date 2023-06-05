package com.xamarsia.store.service;

import com.xamarsia.store.dto.ItemRequestDto;
import com.xamarsia.store.entity.Category;
import com.xamarsia.store.entity.Item;
import com.xamarsia.store.repository.ItemRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class ItemService {
    private final ItemRepository repository;
    private final CategoryService categoryService;

    public List<Item> allItems() {
        return repository.findAll();
    }

    public Item getItemById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Item not found with this id: " + id));
    }

    public Item save(@NonNull final ItemRequestDto itemDto) {
        return repository.save(convertToEntity(itemDto));
    }

    public Item addItemCount(@NonNull final Long id, @NonNull final Integer count) {
        if (count < 1) {
            throw new RuntimeException("Invalid count: " + count);
        }
        Item item = getItemById(id);
        item.setCount(item.getCount() + count);
        return repository.save(item);
    }


    public Item reduceItemCount(@NonNull final Long id, @NonNull final Integer count) {

        if (count < 1) {
            throw new RuntimeException("Invalid count: " + count);
        }

        Item item = getItemById(id);
        int newCount = (item.getCount() - count);

        if (newCount < 0) {
            throw new RuntimeException("Invalid count: " + count);
        }

        item.setCount(newCount);
        return repository.save(item);
    }



    public Item update(@NonNull final Long id, @NonNull final ItemRequestDto itemDto) {
        Item item = convertToEntity(itemDto);
        try {
            return update(id, item);
        } catch (Exception e) {
            System.out.println("Failed to replace item" + e);
        }
        return item;
    }

    public Item update(@NonNull final Long id, @NonNull final Item newItem) throws Exception {


        Item item = getItemById(id);

        item.setDescription(newItem.getDescription());
        item.setPrice(newItem.getPrice());
        item.setTitle(newItem.getTitle());
        item.setImage(newItem.getImage());
        item.setDiscount(newItem.getDiscount());
        item.setCount(newItem.getCount());
        item.setCartItems(newItem.getCartItems());
        item.setCategories(newItem.getCategories());

        return repository.save(item);
    }

    private Item convertToEntity(ItemRequestDto itemDto) {
        Item item = new Item();

        item.setPrice(itemDto.getPrice());
        item.setDescription(itemDto.getDescription());
        item.setTitle(itemDto.getTitle());
        item.setImage(itemDto.getImage());
        item.setDiscount(itemDto.getDiscount());
        item.setCount(itemDto.getCount());
        Set<Category> categories = new HashSet<>();

        for (Long categoryId : itemDto.getCategories()) {
            Category category = categoryService.getCategoryById(categoryId);
            categories.add(category);
        }
        item.setCategories(categories);

        return item;
    }


}
