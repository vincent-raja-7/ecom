package com.infinitetechies.ecom.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product title is required")
    @Size(min = 3, max = 100, message = "Product title must be between 3 and 100 characters")
    private String title;

    @NotBlank(message = "Product brand is required")
    @Size(min = 3, max = 100, message = "Product brand must be between 3 and 100 characters")
    private String brand;

    @NotBlank(message = "Product model is required")
    @Size(min = 3, max = 100, message = "Product model must be between 3 and 100 characters")
    private String model;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Image URL is required")
    @Size(max = 2048, message = "Image URL cannot exceed 2048 characters")
    @Pattern(regexp = "^(http|https)://.*$", message = "Image URL must be a valid URL starting with http or https")
    private String imageUrl;
}
