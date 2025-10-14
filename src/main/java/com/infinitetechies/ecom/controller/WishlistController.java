package com.infinitetechies.ecom.controller;

import com.infinitetechies.ecom.model.dto.response.WishlistResponse;
import com.infinitetechies.ecom.service.inf.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final IWishlistService wishlistService;

    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist() {
        WishlistResponse wishlist = wishlistService.getWishlist();
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable Long productId) {
        WishlistResponse wishlist = wishlistService.addToWishlist(productId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<WishlistResponse> removeFromWishlist(@PathVariable Long productId) {
        WishlistResponse wishlist = wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok(wishlist);
    }
}

