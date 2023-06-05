package com.xamarsia.store.controller;

import com.xamarsia.store.dto.order.AddCartItemRequestDto;
import com.xamarsia.store.entity.Orders;
import com.xamarsia.store.model.OrderModelAssembler;
import com.xamarsia.store.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
//@RequestMapping("/cart")
@AllArgsConstructor
public class OrderController {

    private final OrderService service;
    private final OrderModelAssembler assembler;

    @GetMapping("/cart")
    public CollectionModel<EntityModel<Orders>> all() {

        List<EntityModel<Orders>> order = service.getAllOrder().stream().map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(order, linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping("/cart/{id}")
    public EntityModel<Orders> one(@PathVariable Long id) {
        Orders order = service.getOrderById(id);
        return assembler.toModel(order);
    }

    @PostMapping("/cart")
    public ResponseEntity<?> createCart() throws URISyntaxException {
        return createResponseEntity(service.createCart());
    }

    @PutMapping("cart/{id}/order")
    public ResponseEntity<?> newOrder(@PathVariable Long id) {
        return createResponseEntity(service.createOrder(id));
    }

    @PutMapping("/order/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return createResponseEntity(service.cancel(id));
    }

    @PutMapping("/order/{id}/completed")
    public ResponseEntity<?> setCompleted(@PathVariable Long id) {
        return createResponseEntity(service.setCompleted(id));
    }

    @PutMapping("/order/{id}/setReadyForPickup")
    public ResponseEntity<?> setReadyForPickup(@PathVariable Long id) {
        return createResponseEntity(service.setReadyForPickup(id));
    }

    @PutMapping("cart/{id}")
    ResponseEntity<?> updateOrderItem(@PathVariable Long id, @RequestBody AddCartItemRequestDto temsDto) {

        return createResponseEntity(service.updateCartItem(id, temsDto));
    }


    @DeleteMapping("cart/{id}")
    public String deleteCart(@PathVariable final Long id) {

        return "Cart data can't be deleted! Deleting a cart contradicts the logic of the program.";
    }

    @DeleteMapping("order/{id}")
    public String deleteOrder(@PathVariable final Long id) {

        return "Order data can't be deleted! Deleting a order contradicts the logic of the program.";
    }

    private ResponseEntity<?> createResponseEntity(Orders order) {
        EntityModel<Orders> entityModel = assembler.toModel(order);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}