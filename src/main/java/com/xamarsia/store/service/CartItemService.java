package com.xamarsia.store.service;

import com.xamarsia.store.dto.cartItem.CartItemRequestDto;
import com.xamarsia.store.dto.cartItem.CartItemUpdateRequestDto;
import com.xamarsia.store.entity.CartItem;
import com.xamarsia.store.entity.Item;
import com.xamarsia.store.repository.CartItemRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@AllArgsConstructor
@Service
public class CartItemService {
    private final CartItemRepository repository;

    private final ItemService itemService;

    public List<CartItem> all() {
        return repository.findAll();
    }


    public CartItem save(@NonNull final CartItem cartItem) {
        return repository.save(cartItem);
    }

    public CartItem replaceItemsCount(@NonNull final Long id, @NonNull final Integer count) throws Exception {
        CartItem cartItem = getCartItemById(id);
        cartItem.setCount(count);
        return repository.save(cartItem);
    }


    public CartItem getCartItemById(@NonNull final Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Cart item not found with this id: " + id));
    }


    public CartItem create(@NonNull final CartItemRequestDto itemDto) throws URISyntaxException {

        if (itemDto.getItemID() == null) {
            throw new RuntimeException("Request haven't item_id for creating new Cart item");
        }

        Item item = itemService.getItemById(itemDto.getItemID());
        int count = itemDto.getCount();

        int itemCount = item.getCount();
        if (count < 1) {
            throw new RuntimeException("Invalid item count: " + count);
        }

        if (count > itemCount) {
            throw new RuntimeException("Not enough items to reserve.\n \t Number of items in the store: " + itemCount +
                    "\n \t Number of items your order: " + count);
        }

//        orderService.getOrderById(itemDto.getOrderId());
        itemService.reduceItemCount(item.getId(), count);
        CartItem cartItem = convertToEntity(itemDto);

        return save(cartItem);
    }



    public CartItem replaceCount(@NonNull final Long id, @NonNull final CartItemUpdateRequestDto updateItemDto) {
        CartItem cartItem = getCartItemById(id);

        Item item = itemService.getItemById(cartItem.getItem().getId());

        int oldCount = cartItem.getCount();
        int newCount = updateItemDto.getCount();

        if (oldCount == newCount) {
            return cartItem;
        } else if (oldCount > newCount) {
            itemService.addItemCount(item.getId(), oldCount - newCount);
        } else {
            itemService.reduceItemCount(item.getId(), newCount - oldCount);
        }

        try {
            return replaceItemsCount(id, newCount);
        } catch (Exception e) {
            System.out.println("Failed to replace cart item" + e);
        }
        return cartItem;
    }


    public void delete(@PathVariable final Long id) {
        CartItem cartItem = getCartItemById(id);
        Item item = itemService.getItemById(cartItem.getItem().getId());

        itemService.addItemCount(item.getId(), cartItem.getCount());

        repository.deleteById(id);
    }



    private CartItem convertToEntity(CartItemRequestDto itemDto) {
        CartItem cartItem = new CartItem();

        cartItem.setCount(itemDto.getCount());

        Item item = itemService.getItemById(itemDto.getItemID());
        cartItem.setItem(item);

        return cartItem;
    }


}
