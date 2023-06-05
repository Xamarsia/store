package com.xamarsia.store.dto.item;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ItemRequestDto {
    private String title;
    private byte[] image = null;
    private String description = null;
    private float price;
    private float discount = 0.0f;
    private int count = 0;
    private Set<Long> categories = new HashSet<>();
}
