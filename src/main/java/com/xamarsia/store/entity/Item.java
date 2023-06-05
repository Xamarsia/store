package com.xamarsia.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 200, nullable = false)
    private String title;

    private byte[] image;

    @Column(length = 2000)
    private String description;

    @Column(scale = 2, nullable = false)
    private float price;

    @Column(scale = 2)
    private float discount;

    private int count;

    @ManyToMany
    @JoinTable(name = "items_categories",
            joinColumns = @JoinColumn(name = "item_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "item")
    private Set<CartItem> cartItems = new HashSet<>();
}
