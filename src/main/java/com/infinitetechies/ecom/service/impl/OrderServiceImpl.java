package com.infinitetechies.ecom.service.impl;

import com.infinitetechies.ecom.exception.BadRequestException;
import com.infinitetechies.ecom.exception.ResourceNotFoundException;
import com.infinitetechies.ecom.exception.UnauthorizedException;
import com.infinitetechies.ecom.model.*;
import com.infinitetechies.ecom.model.dto.request.OrderRequest;
import com.infinitetechies.ecom.model.dto.request.OrderStatusUpdateRequest;
import com.infinitetechies.ecom.model.dto.response.OrderItemResponse;
import com.infinitetechies.ecom.model.dto.response.OrderResponse;
import com.infinitetechies.ecom.model.enums.OrderStatus;
import com.infinitetechies.ecom.model.enums.UserRole;
import com.infinitetechies.ecom.repository.CartRepository;
import com.infinitetechies.ecom.repository.OrderRepository;
import com.infinitetechies.ecom.repository.ProductRepository;
import com.infinitetechies.ecom.service.inf.IOrderService;
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
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final IUserService userService;

    @Override
    public List<OrderResponse> getMyOrders() {
        User currentUser = userService.getUserById(1);
        return orderRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        User currentUser = userService.getUserById(1);
        if (!order.getUser().getId().equals(currentUser.getId()) &&
                currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("You can only view your own orders");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        User currentUser = userService.getUserById(1);
        Cart cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = Order.builder()
                .user(currentUser)
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getTitle());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getItems().add(orderItem);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            totalAmount = totalAmount.add(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        // Clear cart after order creation
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        User currentUser = userService.getUserById(3);

        // Only admin can update order status
        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only admin can update order status");
        }

        // Validate status transition
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot update status of delivered or cancelled order");
        }

        order.setStatus(request.getStatus());

        // If order is cancelled, restore product stock
        if (request.getStatus() == OrderStatus.CANCELLED) {
            for (OrderItem orderItem : order.getItems()) {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                productRepository.save(product);
            }
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .items(items)
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        BigDecimal subtotal = orderItem.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productTitle(orderItem.getProduct().getTitle())
                .productBrand(orderItem.getProduct().getBrand())
                .productModel(orderItem.getProduct().getModel())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .subtotal(subtotal)
                .build();
    }
}
