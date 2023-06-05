package com.xamarsia.store.controller;

import com.xamarsia.store.dto.item.ItemRequestDto;
import com.xamarsia.store.entity.Item;
import com.xamarsia.store.model.ItemModelAssembler;
import com.xamarsia.store.service.ItemService;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final ItemModelAssembler assembler;
    private final ItemService service;


    @GetMapping()
    public CollectionModel<EntityModel<Item>> all() {
        List<EntityModel<Item>> items = service.allItems().stream().map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(items, linkTo(methodOn(ItemController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Item> one(@PathVariable Long id) {
        Item item = service.getItemById(id);
        return assembler.toModel(item);
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody ItemRequestDto itemDto) throws URISyntaxException {
        return createResponseEntity(service.save(itemDto));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody ItemRequestDto itemDto) {
        return createResponseEntity(service.update(id, itemDto));

    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable final Long id) {

        return "Item data can't be deleted! Deleting a item contradicts the logic of the program.";
    }

    private ResponseEntity<?> createResponseEntity(Item item) {
        EntityModel<Item> entityModel = assembler.toModel(item);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
