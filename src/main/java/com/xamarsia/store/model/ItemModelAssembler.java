package com.xamarsia.store.model;

import com.xamarsia.store.controller.CategoryController;
import com.xamarsia.store.controller.ItemController;
import com.xamarsia.store.entity.Category;
import com.xamarsia.store.entity.Item;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class ItemModelAssembler implements RepresentationModelAssembler<Item, EntityModel<Item>> {

    @Override
    public EntityModel<Item> toModel(Item item) {

        List<Link> links = new ArrayList<Link>();

        links.add(linkTo(methodOn(ItemController.class).one(item.getId())).withSelfRel());
        links.add(linkTo(methodOn(ItemController.class).all()).withRel("items"));

        for (Category category : item.getCategories()) {
            links.add(linkTo(methodOn(CategoryController.class).one(category.getId())).withRel("categories"));
        }

        return EntityModel.of(item, links);
    }
}