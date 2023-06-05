package com.xamarsia.store.service;

import com.xamarsia.store.Status;
import com.xamarsia.store.dto.order.AddCartItemRequestDto;
import com.xamarsia.store.entity.CartItem;
import com.xamarsia.store.entity.Item;
import com.xamarsia.store.entity.Orders;
import com.xamarsia.store.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class OrderService {

    private final OrderRepository repository;

    private final CartItemService itemService;

    public List<Orders> getAllOrder() {
        return repository.findAll();
    }

    public Orders getOrderById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("Order not found with this id: " + id));
    }

    public Orders createCart() {
        Orders newOrder = new Orders();
        return repository.save(newOrder);
    }

    public Orders createOrder(@NonNull final Long id) {

        Orders order = getOrderById(id);

        if(order.getStatus() != Status.CART) {
            throw new RuntimeException("You cant make order to IN_PROGRESS with this status: " + order.getStatus().name());
        }

        order.setStatus(Status.IN_PROGRESS);

        LocalDateTime localDateTime = LocalDateTime.now();
        order.setCreatedDate(localDateTime);

        order.setPickUpData(localDateTime.plusDays(2).toLocalDate());
        createCart();

        return repository.save(order);
    }


    public Orders cancel(@NonNull Long id) {

        Orders order = getOrderById(id);

        if (order.getStatus() != Status.IN_PROGRESS) {
            throw new RuntimeException("You can't cancel an order that is in the " + order.getStatus() + " status");
        }
        order.setStatus(Status.CANCELLED);
        createCart();

        return repository.save(order);
    }

    public Orders updateOrderItem(@NonNull final Long id, @NonNull final AddCartItemRequestDto itemsDto) {
        Orders order = getOrderById(id);

        if(order.getStatus() != Status.CART) {
            throw new RuntimeException("You can't change items in order that is in the " + order.getStatus() + " status");
        }

        float totalPrice = 0;

        for (CartItem item : order.getCartItems()) {
            item.setOrder(null);
        }

        Set<CartItem> items = new HashSet<>();
        for (Long itemId : itemsDto.getItems()) {
            CartItem cartItem = itemService.getCartItemById(itemId);
            cartItem.setOrder(order);
            items.add(cartItem);

            Item item = cartItem.getItem();
            totalPrice += cartItem.getCount() * (item.getPrice() - item.getDiscount());
        }

        order.setCartItems(items);
        order.setTotalPrice(totalPrice);

        repository.save(order);

        return order;
    }

}