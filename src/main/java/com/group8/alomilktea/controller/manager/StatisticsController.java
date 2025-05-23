package com.group8.alomilktea.controller.manager;

import com.group8.alomilktea.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatisticsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/orders-count")
    public ResponseEntity<Map<String, List<Long>>> getOrdersCount() {
        // Lấy dữ liệu cho trạng thái Pending
        List<Long> pendingCount = Arrays.asList(
                orderRepository.getOrderCountByStatus("Pending", 1), // Tháng 1
                orderRepository.getOrderCountByStatus("Pending", 2), // Tháng 2
                orderRepository.getOrderCountByStatus("Pending", 3), // Tháng 3
                orderRepository.getOrderCountByStatus("Pending", 4), // Tháng 4
                orderRepository.getOrderCountByStatus("Pending", 5), // Tháng 5
                orderRepository.getOrderCountByStatus("Pending", 6), // Tháng 6
                orderRepository.getOrderCountByStatus("Pending", 7), // Tháng 7
                orderRepository.getOrderCountByStatus("Pending", 8), // Tháng 8
                orderRepository.getOrderCountByStatus("Pending", 9), // Tháng 9
                orderRepository.getOrderCountByStatus("Pending", 10), // Tháng 10
                orderRepository.getOrderCountByStatus("Pending", 11), // Tháng 11
                orderRepository.getOrderCountByStatus("Pending", 12)  // Tháng 12
        );

        // Lấy dữ liệu cho trạng thái Cancel
        List<Long> cancelCount = Arrays.asList(
                orderRepository.getOrderCountByStatus("Cancelled", 1),
                orderRepository.getOrderCountByStatus("Cancelled", 2),
                orderRepository.getOrderCountByStatus("Cancelled", 3),
                orderRepository.getOrderCountByStatus("Cancelled", 4),
                orderRepository.getOrderCountByStatus("Cancelled", 5),
                orderRepository.getOrderCountByStatus("Cancelled", 6),
                orderRepository.getOrderCountByStatus("Cancelled", 7),
                orderRepository.getOrderCountByStatus("Cancelled", 8),
                orderRepository.getOrderCountByStatus("Cancelled", 9),
                orderRepository.getOrderCountByStatus("Cancelled", 10),
                orderRepository.getOrderCountByStatus("Cancelled", 11),
                orderRepository.getOrderCountByStatus("Cancelled", 12)
        );

        // Đóng gói dữ liệu vào một Map
        Map<String, List<Long>> result = new HashMap<>();
        result.put("pending", pendingCount);
        result.put("cancel", cancelCount);

        // Trả về dữ liệu dưới dạng JSON
        return ResponseEntity.ok(result);
    }
}

