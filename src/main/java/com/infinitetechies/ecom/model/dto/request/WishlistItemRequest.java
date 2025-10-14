package com.infinitetechies.ecom.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;
}
