package com.Tsimur.Metrics.service;


import com.Tsimur.Metrics.model.Customer;
import com.Tsimur.Metrics.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderMetricsService {
    Set<String> getUniqueCities(List<Order> orders);
    double getTotalIncomeForCompletedOrders(List<Order> orders);
    Optional<String> getMostPopularProduct(List<Order> orders);
    double getAverageCheckForDeliveredOrders(List<Order> orders);
    List<Customer> getCustomersWithMoreThanFiveOrders(List<Order> orders);
}