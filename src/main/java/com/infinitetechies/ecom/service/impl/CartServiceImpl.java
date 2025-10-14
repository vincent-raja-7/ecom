package com.infinitetechies.ecom.service.impl;
import com.infinitetechies.ecom.exception.BadRequestException;
import com.infinitetechies.ecom.exception.ResourceNotFoundException;
import com.infinitetechies.ecom.model.Cart;
import com.infinitetechies.ecom.model.CartItem;
import com.infinitetechies.ecom.model.Product;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.dto.request.CartItemRequest;
import com.infinitetechies.ecom.model.dto.response.CartItemResponse;
import com.infinitetechies.ecom.model.dto.response.CartResponse;
import com.infinitetechies.ecom.repository.CartItemRepository;
import com.infinitetechies.ecom.repository.CartRepository;
import com.infinitetechies.ecom.repository.ProductRepository;
import com.infinitetechies.ecom.service.inf.ICartService;
import com.infinitetechies.ecom.service.inf.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final IUserService userService;

    @Override
    public CartResponse getCart() {
        User currentUser = userService.getUserById(1);
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createCartForUser(currentUser));

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(CartItemRequest request) {
        User currentUser = userService.getUserById(1);
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createCartForUser(currentUser));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException("Not enough stock available");
        }

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(cartItem);
        }

        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long productId, Integer quantity) {
        User currentUser = userService.getUserById(1);
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        if (quantity <= 0) {
            cart.removeItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            if (cartItem.getProduct().getStockQuantity() < quantity) {
                throw new BadRequestException("Not enough stock available");
            }
            cartItem.setQuantity(quantity);
        }

        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long productId) {
        User currentUser = userService.getUserById(1);
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem cartItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    private Cart createCartForUser(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();
        return cartRepository.save(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        BigDecimal subtotal = cartItem.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productTitle(cartItem.getProduct().getTitle())
                .productBrand(cartItem.getProduct().getBrand())
                .productModel(cartItem.getProduct().getModel())
                .productPrice(cartItem.getProduct().getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}
