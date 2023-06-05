package com.xamarsia.store.controller;

import com.xamarsia.store.dto.cartItem.CartItemRequestDto;
import com.xamarsia.store.dto.cartItem.CartItemUpdateRequestDto;
import com.xamarsia.store.entity.CartItem;
import com.xamarsia.store.entity.Item;
import com.xamarsia.store.service.CartItemService;
import com.xamarsia.store.model.CartItemModelAssembler;

import com.xamarsia.store.service.ItemService;
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
@RequestMapping("/cartItems")
@AllArgsConstructor
public class CartItemController {

    private final CartItemModelAssembler assembler;
    private final CartItemService service;


    @GetMapping()
    public CollectionModel<EntityModel<CartItem>> all() {
        List<EntityModel<CartItem>> books = service.all().stream().map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(books, linkTo(methodOn(CartItemController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<CartItem> one(@PathVariable Long id) {
        CartItem cartItem = service.getCartItemById(id);
        return assembler.toModel(cartItem);
    }

    @PostMapping()
    public ResponseEntity<?> upload(@RequestBody CartItemRequestDto itemDto) throws URISyntaxException {
        return createResponseEntity(service.create(itemDto));
    }


    @PutMapping("/{id}")
    ResponseEntity<?> replaceCount(@PathVariable Long id, @RequestBody CartItemUpdateRequestDto updateItemDto) {
        return createResponseEntity(service.replaceCount(id, updateItemDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> createResponseEntity(CartItem cartItem) {
        EntityModel<CartItem> entityModel = assembler.toModel(cartItem);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
