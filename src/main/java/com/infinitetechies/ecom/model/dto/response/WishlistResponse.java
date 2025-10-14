package com.infinitetechies.ecom.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    private Long id;                          // Wishlist ID
    private List<WishlistItemResponse> items;  // List of Wishlist items
}
