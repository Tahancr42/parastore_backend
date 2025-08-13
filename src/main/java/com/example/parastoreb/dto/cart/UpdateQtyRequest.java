package com.example.parastoreb.dto.cart;

import lombok.Data;

@Data
public class UpdateQtyRequest {
    private Long userId;
    private int quantity; // nouvelle quantité (si <=0, on peut supprimer)
}
