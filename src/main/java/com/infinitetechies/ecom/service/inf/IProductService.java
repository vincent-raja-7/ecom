package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.dto.request.ProductRequest;
import com.infinitetechies.ecom.model.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductResponse> searchProducts(String keyword);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
