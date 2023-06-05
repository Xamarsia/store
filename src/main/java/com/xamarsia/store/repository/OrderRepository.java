package com.xamarsia.store.repository;

import com.xamarsia.store.Status;
import com.xamarsia.store.entity.Item;
import com.xamarsia.store.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    @Query("select o from Orders o where o.user.id = :userId")
    List<Orders> findOrdersByUser(@Param("userId") Long userId);

    @Query("select o.status from Orders o where o.user.id = :userId")
    List<Status> findOrderStatusesByUser(@Param("userId") Long userId);




}
