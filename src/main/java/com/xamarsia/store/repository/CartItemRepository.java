package com.xamarsia.store.repository;

import com.xamarsia.store.entity.CartItem;
import com.xamarsia.store.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
