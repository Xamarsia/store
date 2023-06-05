package com.xamarsia.store.dto.order;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AddCartItemRequestDto {
    private Set<Long> items = new HashSet<>();
}
