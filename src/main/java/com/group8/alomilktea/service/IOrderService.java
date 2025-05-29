package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.Order;
import com.group8.alomilktea.entity.User; // Cần import User
import com.group8.alomilktea.entity.ShipmentCompany; // Cần import ShipmentCompany
import com.group8.alomilktea.entity.Promotion; // Cần import Promotion
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IOrderService {
    // ... các phương thức hiện có ...

    Order findById(Integer id);
    Page<Order> getAll(Integer pageNo);
    Page<Order> getOderByStatus(Integer pageNo, String status, Long shipId);
    void updateOrderState(Integer orderId, String newState);
    void deleteById(Integer id);
    List<Order> findOder(Integer userId);
    long count();
    List<Order> findAll();
    Page<Order> findAll(Pageable pageable);
    <S extends Order> S save(S entity);
    int reOnCurrentMonth();
    int reOnCurrentYear();
    int reOnCurrentQuarter();
    float rateCom();
    List<Integer> getMonthlyTotal();
    List<Integer> getQuarterTotal();
    long countPendingOrders();
    long countDoneOrders();
    long countCancelOrders();
    long countShippingOrders();
    int getCompletedOrderRate();
    int countByStatus(String status);
    int countByStatusAndShip(String status, Long shipId);
    long countbyShipID(Long ShipId);
    long countOrder();
    int updateStatus(Long orderId, String status);

    // Phương thức mới để tính tổng tiền đơn hàng từ server
    Double calculateGrandTotalFromServer(User user, Integer shipmentCompanyId, String promotionCode);
}