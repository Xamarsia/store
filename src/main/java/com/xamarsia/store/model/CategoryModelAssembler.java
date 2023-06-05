package com.xamarsia.store.model;

import com.xamarsia.store.controller.CategoryController;
import com.xamarsia.store.controller.ItemController;
import com.xamarsia.store.entity.Category;
import com.xamarsia.store.entity.Item;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler implements RepresentationModelAssembler<Category, EntityModel<Category>> {

    @Override
    public EntityModel<Category> toModel(Category category) {

        List<Link> links = new ArrayList<Link>();

        links.add(linkTo(methodOn(CategoryController.class).one(category.getId())).withSelfRel());


        links.add(linkTo(methodOn(CategoryController.class).all()).withRel("categories"));

        for (Item book : category.getItems()) {
            links.add(linkTo(methodOn(ItemController.class).one(book.getId())).withRel("items"));
        }

        return EntityModel.of(category, links);
    }
}