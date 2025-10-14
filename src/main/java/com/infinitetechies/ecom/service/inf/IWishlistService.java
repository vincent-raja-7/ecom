package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.dto.response.WishlistResponse;

public interface IWishlistService {
    WishlistResponse getWishlist();
    WishlistResponse addToWishlist(Long productId);
    WishlistResponse removeFromWishlist(Long productId);
}
