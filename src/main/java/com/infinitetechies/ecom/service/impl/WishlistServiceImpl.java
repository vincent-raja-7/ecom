package com.infinitetechies.ecom.service.impl;
import com.infinitetechies.ecom.exception.BadRequestException;
import com.infinitetechies.ecom.exception.ResourceNotFoundException;
import com.infinitetechies.ecom.model.Product;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.Wishlist;
import com.infinitetechies.ecom.model.WishlistItem;
import com.infinitetechies.ecom.model.dto.response.ProductResponse;
import com.infinitetechies.ecom.model.dto.response.WishlistItemResponse;
import com.infinitetechies.ecom.model.dto.response.WishlistResponse;
import com.infinitetechies.ecom.repository.ProductRepository;
import com.infinitetechies.ecom.repository.WishlistItemRepository;
import com.infinitetechies.ecom.repository.WishlistRepository;
import com.infinitetechies.ecom.service.inf.IUserService;
import com.infinitetechies.ecom.service.inf.IWishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final IUserService userService;

    @Override
    public WishlistResponse getWishlist() {
        User currentUser = userService.getUserById(1);
        Wishlist wishlist = wishlistRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createWishlistForUser(currentUser));

        return mapToWishlistResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse addToWishlist(Long productId) {
        User currentUser = userService.getUserById(1);
        Wishlist wishlist = wishlistRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createWishlistForUser(currentUser));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        boolean alreadyExists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (alreadyExists) {
            throw new BadRequestException("Product already in wishlist");
        }

        WishlistItem wishlistItem = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        wishlist.getItems().add(wishlistItem);
        wishlistItemRepository.save(wishlistItem);

        return mapToWishlistResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse removeFromWishlist(Long productId) {
        User currentUser = userService.getUserById(1);
        Wishlist wishlist = wishlistRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        WishlistItem wishlistItem = wishlist.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Product not in wishlist"));

        wishlist.getItems().remove(wishlistItem);
        wishlistItemRepository.delete(wishlistItem);

        return mapToWishlistResponse(wishlist);
    }

    private Wishlist createWishlistForUser(User user) {
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .build();
        return wishlistRepository.save(wishlist);
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        List<WishlistItemResponse> items = wishlist.getItems().stream()
                .map(this::mapToWishlistItemResponse)
                .collect(Collectors.toList());

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .items(items)
                .build();
    }

    private WishlistItemResponse mapToWishlistItemResponse(WishlistItem wishlistItem) {
        return WishlistItemResponse.builder()
                .id(wishlistItem.getId())
                .product(mapToProductResponse(wishlistItem.getProduct()))
                .build();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                // Assuming Product has title, brand, model fields now instead of name
                .title(product.getTitle())
                .brand(product.getBrand())
                .model(product.getModel())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getFirstName() + " " + product.getSeller().getLastName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

