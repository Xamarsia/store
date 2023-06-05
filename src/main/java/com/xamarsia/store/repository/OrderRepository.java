package com.xamarsia.store.repository;

import com.xamarsia.store.entity.Item;
import com.xamarsia.store.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
