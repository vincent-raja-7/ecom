package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.dto.request.OrderRequest;
import com.infinitetechies.ecom.model.dto.request.OrderStatusUpdateRequest;
import com.infinitetechies.ecom.model.dto.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    List<OrderResponse> getMyOrders();
    OrderResponse getOrderById(Long id);
    OrderResponse createOrder(OrderRequest request);
    OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request);
}
