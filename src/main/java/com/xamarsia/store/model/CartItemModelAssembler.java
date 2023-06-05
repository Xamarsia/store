package com.xamarsia.store.model;

import com.xamarsia.store.controller.CartItemController;
import com.xamarsia.store.controller.ItemController;
import com.xamarsia.store.entity.CartItem;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CartItemModelAssembler implements RepresentationModelAssembler<CartItem, EntityModel<CartItem>> {

    @Override
    public EntityModel<CartItem> toModel(CartItem cartItem) {

        List<Link> links = new ArrayList<Link>();

        links.add(linkTo(methodOn(CartItemController.class).one(cartItem.getId())).withSelfRel());

        links.add(linkTo(methodOn(ItemController.class).one(cartItem.getItem().getId())).withRel("item"));

        return EntityModel.of(cartItem, links);
    }
}
