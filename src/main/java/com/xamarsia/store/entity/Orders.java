package com.xamarsia.store.entity;

import com.xamarsia.store.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDateTime createdDate = null;

    @Column(scale = 2)
    private float totalPrice = 0;

    @Column(scale = 2)
    private float discount = 0;

    private Status status = Status.CART;

    private LocalDate pickUpData = null;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private Set<CartItem> cartItems = new HashSet<>();

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
