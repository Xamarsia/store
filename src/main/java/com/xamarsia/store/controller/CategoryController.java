package com.xamarsia.store.controller;

import com.xamarsia.store.entity.Category;
import com.xamarsia.store.model.CategoryModelAssembler;
import com.xamarsia.store.service.CategoryService;
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
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService service;
    private final CategoryModelAssembler assembler;

    @GetMapping()
    public CollectionModel<EntityModel<Category>> all() {
        List<EntityModel<Category>> categories = service.all().stream().map(assembler::toModel).collect(Collectors.toList());

        return CollectionModel.of(categories, linkTo(methodOn(CategoryController.class).all()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Category> one(@PathVariable Long id) {
        Category category = service.getCategoryById(id);
        return assembler.toModel(category);
    }

    @PostMapping()
    public ResponseEntity<?> upload(@RequestBody Category category) throws URISyntaxException {
        return createResponseEntity(service.save(category));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> replace(@PathVariable Long id, @RequestBody Category newCategory) {

        try {
            return createResponseEntity(service.replace(id, newCategory));
        } catch (Exception e) {
            System.out.println("Failed to replace category" + e);
        }
        return createResponseEntity(newCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {

        service.delete(id);
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> createResponseEntity(Category category) {
        EntityModel<Category> entityModel = assembler.toModel(category);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
