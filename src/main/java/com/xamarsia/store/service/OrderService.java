package com.xamarsia.store.service;

import com.xamarsia.store.Status;
import com.xamarsia.store.dto.order.AddCartItemRequestDto;
import com.xamarsia.store.entity.*;
import com.xamarsia.store.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<Orders> getAllOrdersByUser() {
        List<Orders> orders = new ArrayList<>();
        orders = repository.findOrdersByUser(getUser().getId());

        return orders;
    }

    private Boolean isUserAlreadyHasCart() {
        return repository.findOrderStatusesByUser(getUser().getId()).contains(Status.CART);
    }

    public Orders getOrderById(Long id) {
        Orders order = repository.findById(id).orElseThrow(
                () -> new RuntimeException("Order not found with this id: " + id));

        User user = getUser();
        if(user.getId() != order.getUser().getId() && user.getRole() != Role.ADMIN){
            throw new RuntimeException("You can't change not your order");
        }

        return order;
    }

    private User getUser(){
        User user = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
             user = (User)authentication.getPrincipal();

        } catch (Exception e) {
            throw  new RuntimeException("Failed to find user", e);
        }
        return user;
    }

    public Orders createCart(User user) {

        Orders newOrder = new Orders();
        newOrder.setUser(user);
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
        createCartForAuthorizedUser();

        return repository.save(order);
    }


    //TODO User/Administrator permission
    public Orders cancel(@NonNull Long id) {

        Orders order = getOrderById(id);

        if (order.getStatus() != Status.IN_PROGRESS) {
            throw new RuntimeException("You can't cancel an order that is in the " + order.getStatus() + " status");
        }
        order.setStatus(Status.CANCELLED);
        createCartForAuthorizedUser();

        return repository.save(order);
    }


    //TODO Administrator permission
    public Orders setReadyForPickup(@NonNull Long id) {

        Orders order = getOrderById(id);

        if (order.getStatus() != Status.IN_PROGRESS) {
            throw new RuntimeException("You can't set ready for pickup status that is in the " + order.getStatus() + " status");
        }
        order.setStatus(Status.READY_FOR_PICKUP);

        return repository.save(order);
    }

    //TODO Administrator permission
    public Orders setCompleted(@NonNull Long id) {

        Orders order = getOrderById(id);

        if (order.getStatus() != Status.READY_FOR_PICKUP) {
            throw new RuntimeException("You can't set completed status that is in the " + order.getStatus() + " status");
        }
        order.setStatus(Status.COMPLETED);

        return repository.save(order);
    }


    //TODO User permission
    public Orders updateCartItem(@NonNull final Long id, @NonNull final AddCartItemRequestDto itemsDto) {
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

    private Orders createCartForAuthorizedUser() {
       return createCart(getUser());
    }
}