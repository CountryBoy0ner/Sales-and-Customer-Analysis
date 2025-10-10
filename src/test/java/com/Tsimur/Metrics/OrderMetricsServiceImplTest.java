package com.Tsimur.Metrics;



import com.Tsimur.Metrics.model.*;
import com.Tsimur.Metrics.service.OrderMetricsService;
import com.Tsimur.Metrics.service.OrderMetricsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderMetricsServiceImplTest {

    private OrderMetricsService service;

    private Customer c1;
    private Customer c2;
    private Customer c3;

    private Order o1_delivered_c1;
    private Order o2_delivered_c2;
    private Order o3_processing_c3;
    private Order o4_cancelled_c1;

    private List<Order> baseOrders;

    @BeforeEach
    void setUp() {
        service = new OrderMetricsServiceImpl();

        c1 = new Customer("1", "Ivan Petrov", "cqwd1@example.com",
                LocalDateTime.now().minusDays(50), 28, "Minsk");
        c2 = new Customer("2", "Pavel ыSidorov", "dc2a@example.com",
                LocalDateTime.now().minusDays(30), 34, "Gomel");
        c3 = new Customer("3", "Olga Ivanova", "2fc31@example.com",
                LocalDateTime.now().minusDays(5), 22, "Minsk");

        OrderItem phone2x = new OrderItem("Phone", 2, 500.0, Category.ELECTRONICS);
        OrderItem book1x  = new OrderItem("Book", 1, 20.0, Category.BOOKS);
        OrderItem laptop1 = new OrderItem("Laptop", 1, 1000.0, Category.ELECTRONICS);
        OrderItem book4x  = new OrderItem("Book", 4, 15.0, Category.BOOKS);
        OrderItem toy3x   = new OrderItem("Toy", 3, 10.0, Category.TOYS);

        o1_delivered_c1 = new Order("1", LocalDateTime.now().minusDays(10), c1,
                Arrays.asList(phone2x, book1x), OrderStatus.DELIVERED);
        o2_delivered_c2 = new Order("2", LocalDateTime.now().minusDays(8), c2,
                List.of(laptop1), OrderStatus.DELIVERED);
        o3_processing_c3 = new Order("3", LocalDateTime.now().minusDays(2), c3,
                List.of(book4x), OrderStatus.PROCESSING);
        o4_cancelled_c1 = new Order("4", LocalDateTime.now().minusDays(1), c1,
                List.of(toy3x), OrderStatus.CANCELLED);

        baseOrders = List.of(o1_delivered_c1, o2_delivered_c2, o3_processing_c3, o4_cancelled_c1);
    }


    @Test
    void getUniqueCities_basic() {
        Set<String> cities = service.getUniqueCities(baseOrders);
        assertEquals(Set.of("Minsk", "Gomel"), cities);
    }

    @Test
    void getUniqueCities_nullOrEmpty() {
        assertTrue(service.getUniqueCities(null).isEmpty());
        assertTrue(service.getUniqueCities(Collections.emptyList()).isEmpty());
    }


    @Test
    void getTotalIncomeForCompletedOrders_basic() {
        double income = service.getTotalIncomeForCompletedOrders(baseOrders);
        assertEquals(2020.0, income, 1e-9);
    }

    @Test
    void getTotalIncomeForCompletedOrders_noDelivered() {
        List<Order> noneDelivered = List.of(
                new Order("1", LocalDateTime.now(), c1, List.of(), OrderStatus.NEW),
                new Order("2", LocalDateTime.now(), c2, null, OrderStatus.CANCELLED)
        );
        assertEquals(0.0, service.getTotalIncomeForCompletedOrders(noneDelivered), 1e-9);
    }


    @Test
    void getMostPopularProduct_basic() {
        Optional<String> popular = service.getMostPopularProduct(baseOrders);
        assertTrue(popular.isPresent());
        assertEquals("Book", popular.get());
    }

    @Test
    void getMostPopularProduct_emptyOrNull() {
        assertTrue(service.getMostPopularProduct(Collections.emptyList()).isEmpty());
        assertTrue(service.getMostPopularProduct(null).isEmpty());
    }


    @Test
    void getAverageCheckForDeliveredOrders_basic() {
        double avg = service.getAverageCheckForDeliveredOrders(baseOrders);
        assertEquals(1010.0, avg, 1e-9);
    }

    @Test
    void getAverageCheckForDeliveredOrders_noDelivered() {
        List<Order> noneDelivered = List.of(o3_processing_c3, o4_cancelled_c1);
        assertEquals(0.0, service.getAverageCheckForDeliveredOrders(noneDelivered), 1e-9);
    }


    @Test
    void getCustomersWithMoreThanFiveOrders_basic() {
        List<Order> manyOrders = new ArrayList<>(baseOrders);

        for (int i = 0; i < 4; i++) {
            manyOrders.add(new Order("c1_extra_" + i, LocalDateTime.now().minusDays(20 - i),
                    c1, List.of(), OrderStatus.NEW));
        }

        for (int i = 0; i < 3; i++) {
            manyOrders.add(new Order("c2_extra_" + i, LocalDateTime.now().minusDays(15 - i),
                    c2, List.of(), OrderStatus.PROCESSING));
        }

        List<Customer> result = service.getCustomersWithMoreThanFiveOrders(manyOrders);
        assertEquals(1, result.size());
        assertEquals(c1, result.get(0));
    }

    @Test
    void getCustomersWithMoreThanFiveOrders_emptyOrNull() {
        assertTrue(service.getCustomersWithMoreThanFiveOrders(Collections.emptyList()).isEmpty());
        assertTrue(service.getCustomersWithMoreThanFiveOrders(null).isEmpty());
    }
}