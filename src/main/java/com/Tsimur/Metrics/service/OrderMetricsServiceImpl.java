package com.Tsimur.Metrics.service;


import com.Tsimur.Metrics.model.Customer;
import com.Tsimur.Metrics.model.Order;
import com.Tsimur.Metrics.model.OrderStatus;
import com.Tsimur.Metrics.model.OrderItem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderMetricsServiceImpl implements OrderMetricsService {


    @Override
    public Set<String> getUniqueCities(List<Order> orders) {
        if (orders == null) return Collections.emptySet();
        return orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull) //todo
                .map(Customer::getCity)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public double getTotalIncomeForCompletedOrders(List<Order> orders) {
        if (orders == null) return 0.0;
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(OrderMetricsServiceImpl::orderTotal)
                .sum();
    }

    private static double orderTotal(Order order) {
        if (order == null || order.getItems() == null) return 0.0;
        return order.getItems().stream()
                .filter(Objects::nonNull)
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    @Override
    public Optional<String> getMostPopularProduct(List<Order> orders) {
        if (orders == null) return Optional.empty();

        Map<String, Integer> qtyByProduct = orders.stream()
                .filter(Objects::nonNull)
                .map(Order::getItems)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        return qtyByProduct.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    @Override
    public double getAverageCheckForDeliveredOrders(List<Order> orders) {
        if (orders == null) return 0.0;
        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(OrderMetricsServiceImpl::orderTotal)
                .average()
                .orElse(0.0);
    }


    @Override
    public List<Customer> getCustomersWithMoreThanFiveOrders(List<Order> orders) {
        if (orders == null) return Collections.emptyList();

        return orders.stream()
                .map(Order::getCustomer)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}