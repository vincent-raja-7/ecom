package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.dto.request.CartItemRequest;
import com.infinitetechies.ecom.model.dto.response.CartResponse;

public interface ICartService {
    CartResponse getCart();
    CartResponse addToCart(CartItemRequest request);
    CartResponse updateCartItem(Long productId, Integer quantity);
    CartResponse removeFromCart(Long productId);
}
