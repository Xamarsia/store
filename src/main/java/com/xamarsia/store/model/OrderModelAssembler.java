package com.xamarsia.store.model;

import com.xamarsia.store.Status;
import com.xamarsia.store.controller.CartItemController;
import com.xamarsia.store.controller.OrderController;
import com.xamarsia.store.entity.CartItem;
import com.xamarsia.store.entity.Orders;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<Orders, EntityModel<Orders>> {

    @Override
    public EntityModel<Orders> toModel(Orders order) {

        List<Link> links = new ArrayList<Link>();

        links.add(linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel());
        links.add(linkTo(methodOn(OrderController.class).all()).withRel("orders"));


        Set<CartItem> cartItems = order.getCartItems();
        for (CartItem item : cartItems) {
            links.add(linkTo(methodOn(CartItemController.class).one(item.getId())).withRel("cart_items"));
        }

        if(order.getStatus() == Status.CART && !cartItems.isEmpty()){
            links.add(linkTo(methodOn(OrderController.class).newOrder(order.getId())).withRel("make_order"));
        }

        if (order.getStatus() == Status.IN_PROGRESS) {
            links.add(linkTo(methodOn(OrderController.class).cancel(order.getId())).withRel("cancel"));
        }

        return EntityModel.of(order, links);
    }
}