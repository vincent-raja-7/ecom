package com.infinitetechies.ecom.controller;
import com.infinitetechies.ecom.model.dto.request.CartItemRequest;
import com.infinitetechies.ecom.model.dto.response.CartResponse;
import com.infinitetechies.ecom.service.inf.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        CartResponse cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        CartResponse cart = cartService.addToCart(request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        CartResponse cart = cartService.updateCartItem(productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long productId) {
        CartResponse cart = cartService.removeFromCart(productId);
        return ResponseEntity.ok(cart);
    }
}

