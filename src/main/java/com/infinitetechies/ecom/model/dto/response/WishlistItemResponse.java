package com.infinitetechies.ecom.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItemResponse {
    private Long id;            // WishlistItem ID
    private ProductResponse product;  // Details of the product in wishlist
}
