package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.common.enums.ProductAttribute;
import com.group8.alomilktea.entity.*;
import com.group8.alomilktea.repository.OrderRepository;
import com.group8.alomilktea.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date; // Thêm import này
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private ICartService cartService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IShipmentCompany shipmentCompanyService;

    @Autowired
    private IPromotionService promotionService;

    // Constructor được ưu tiên cho injection, nhưng @Autowired trên field vẫn hoạt động
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Double calculateGrandTotalFromServer(User user, Integer shipmentCompanyId, String promotionName) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        List<Cart> cartItems = cartService.findByUserId(user.getUserId());
        if (cartItems.isEmpty()) {
            return 0.0;
        }

        double productsTotal = 0.0;
        for (Cart cartItem : cartItems) {
            Product product = productService.findById(cartItem.getProduct().getProId());
            if (product == null || cartItem.getId() == null || cartItem.getId().getSize() == null) {
                System.err.println("Invalid cart item, skipping: " + cartItem);
                continue;
            }

            ProductDetail productDetail = productService.findPriceByProductIdAndSize(
                    product.getProId(),
                    ProductAttribute.getEnum(cartItem.getId().getSize())
            );

            if (productDetail != null && productDetail.getPrice() != null) {
                productsTotal += productDetail.getPrice() * cartItem.getQuantity();
            } else {
                System.err.println("Price not found for product ID: " + product.getProId() + " and size: " + cartItem.getId().getSize());
            }
        }

        double shippingCost = 0.0;
        if (shipmentCompanyId != null) {
            ShipmentCompany sc = shipmentCompanyService.findById(shipmentCompanyId);
            if (sc != null && sc.getPrice() != null) {
                shippingCost = sc.getPrice();
            }
        }

        double discountAmount = 0.0;
        if (promotionName != null && !promotionName.isEmpty()) {
            Optional<Promotion> optPromotion = promotionService.findByName(promotionName); // Sửa: Sử dụng Optional
            if (optPromotion.isPresent()) {
                Promotion promotion = optPromotion.get();
                // Kiểm tra thêm is_active và validity
                boolean isActive = promotion.getIsActive() != null && promotion.getIsActive() == 1;
                boolean isValidDate = promotion.getValidity() == null || promotion.getValidity().after(new Date()); // java.util.Date

                if (isActive && isValidDate && promotion.getDiscountRate() != null) {
                    discountAmount = productsTotal * (promotion.getDiscountRate() / 100.0);
                }
            }
        }
        return productsTotal + shippingCost - discountAmount;
    }

    @Override
    public Page<Order> getAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return orderRepository.findAllCustom(pageable);
    }

    @Override
    public Page<Order> getOderByStatus(Integer pageNo, String status,Long shipId) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return orderRepository.findOderByStatus(pageable,status,shipId);
    }

    @Override
    @Transactional
    public void updateOrderState(Integer orderId, String newState) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(newState);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<Order> findOder(Integer userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    @Override
    public long count() {
        return orderRepository.count();
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public <S extends Order> S save(S entity) {
        return orderRepository.save(entity);
    }

    @Override
    public int reOnCurrentMonth() {
        return orderRepository.revenueOnCurrentMonth();
    }

    @Override
    public int reOnCurrentYear() {
        return orderRepository.revenueOnCurrentYear();
    }

    @Override
    public int reOnCurrentQuarter() {
        return orderRepository.revenueOnCurrentQuarter();
    }

    @Override
    public float rateCom() {
        return orderRepository.rateCompleted();
    }

    @Override
    public List<Integer> getMonthlyTotal() {
        return orderRepository.getMonthlyTotal();
    }

    @Override
    public List<Integer> getQuarterTotal() {
        return orderRepository.getQuarterTotal();
    }
    @Override
    public long countPendingOrders() {
        return orderRepository.countOrdersByStatus("Pending");
    }

    @Override
    public long countDoneOrders() {
        return orderRepository.countOrdersByStatus("Delivered");
    }

    @Override
    public long countCancelOrders() {
        return orderRepository.countOrdersByStatus("Cancelled");
    }

    @Override
    public long countShippingOrders() {
        return orderRepository.countOrdersByStatus("Shipping");
    }

    @Override
    public int getCompletedOrderRate() {
        return orderRepository.rateCompleted();
    }

    @Override
    public int countByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public int countByStatusAndShip(String status, Long shipId) {
        return orderRepository.countByStatusAndShip(status,shipId);
    }

    @Override
    public long countbyShipID(Long ShipId) {
        return orderRepository.countbyShipID(ShipId);
    }
    @Override
    public long countOrder() {
        return orderRepository.countOrders();
    }

    @Override
    @Transactional
    public int updateStatus(Long orderId, String status) {
        return orderRepository.updateStatus(orderId,status);
    }
}