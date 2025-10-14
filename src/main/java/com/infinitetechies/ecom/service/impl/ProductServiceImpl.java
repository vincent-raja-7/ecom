package com.infinitetechies.ecom.service.impl;

import com.infinitetechies.ecom.exception.ResourceNotFoundException;
import com.infinitetechies.ecom.exception.UnauthorizedException;
import com.infinitetechies.ecom.model.Product;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.dto.request.ProductRequest;
import com.infinitetechies.ecom.model.dto.response.ProductResponse;
import com.infinitetechies.ecom.model.enums.UserRole;
import com.infinitetechies.ecom.repository.ProductRepository;
import com.infinitetechies.ecom.service.inf.IProductService;
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
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final IUserService userService;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        User currentUser = userService.getUserById(2);

        if (currentUser.getRole() != UserRole.SELLER && currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only sellers can create products");
        }

        Product product = Product.builder()
                .title(request.getTitle())
                .brand(request.getBrand())
                .model(request.getModel())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .seller(currentUser)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User currentUser = userService.getUserById(3);

        if (!product.getSeller().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("You can only update your own products");
        }

        product.setTitle(request.getTitle());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        User currentUser = userService.getUserById(3);

        if (!product.getSeller().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .brand(product.getBrand())
                .model(product.getModel())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .sellerId(product.getSeller().getId())
                .imageUrl(product.getImageUrl())
                .sellerName(product.getSeller().getFirstName() + " " + product.getSeller().getLastName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
