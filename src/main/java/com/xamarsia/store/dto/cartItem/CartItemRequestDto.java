package com.xamarsia.store.dto.cartItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemRequestDto {

    Integer count;
    private Long itemID;
//    private Long orderId;
}