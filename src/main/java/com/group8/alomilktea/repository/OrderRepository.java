package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_UserId(int userId);

    @Query(nativeQuery = true, value = "SELECT * FROM orders ORDER BY date DESC")
    Page<Order> findAllCustom(Pageable pageable);
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.shipmentCompany.shipCid = :shipId ORDER BY o.date DESC")
    Page<Order> findOderByStatus(Pageable pageable,@Param("status") String status,@Param("shipId") Long shipId);
    @Query("""
    SELECT o FROM Order o WHERE o.user.userId = :userId
        """)
    List<Order> findOrderByUserId(@Param("userId") Integer userId);

    @Query(value = "SELECT IFNULL(SUM(CASE " +
            "WHEN currency = 'VNĐ' THEN total " +
            "WHEN currency = 'USD' THEN total * 24390.243902 " +
            "END), 0) AS total_month " +
            "FROM orders " +
            "WHERE MONTH(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = MONTH(CURRENT_DATE()) " +
            "AND status = 'Delivered';",
            nativeQuery = true)
    int revenueOnCurrentMonth();

    @Query(value = "SELECT IFNULL(SUM(CASE " +
            "WHEN currency = 'VNĐ' THEN total " +
            "WHEN currency = 'USD' THEN total * 24390.243902 " +
            "END), 0) AS total_year " +
            "FROM orders " +
            "WHERE YEAR(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = YEAR(CURRENT_DATE()) " +
            "AND status = 'Delivered';",
            nativeQuery = true)
    int revenueOnCurrentYear();

    @Query(value = "SELECT IFNULL(SUM(CASE WHEN status = 'Delivered' THEN 1 ELSE 0 END) / COUNT(*) * 100, 0) AS completed_rate " +
            "FROM orders",
            nativeQuery = true)
    int rateCompleted();

    @Query(value = "SELECT IFNULL(SUM(CASE " +
            "WHEN currency = 'VNĐ' THEN total " +
            "WHEN currency = 'USD' THEN total * 24390.243902 " +
            "END), 0) AS total_quarter " +
            "FROM orders " +
            "WHERE QUARTER(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = QUARTER(CURRENT_DATE()) " +
            "AND status = 'Delivered';",
            nativeQuery = true)
    int revenueOnCurrentQuarter();

    @Query(nativeQuery = true, value =
            "WITH AllMonths AS (\r\n"
                    + "  SELECT 1 AS month_number UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 \r\n"
                    + "  UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 \r\n"
                    + "  UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12\r\n"
                    + ")\r\n"
                    + "SELECT  IFNULL(SUM(CASE WHEN o.currency = \"VNĐ\" THEN o.total\r\n"
                    + "                                        WHEN o.currency = \"USD\" THEN o.total * 23000\r\n"
                    + "                                   END), 0) AS total_sum, m.month_number\r\n"
                    + "FROM AllMonths m\r\n"
                    + "LEFT JOIN wck.orders o ON MONTH(STR_TO_DATE(o.date, '%d/%m/%Y %H:%i:%s')) = m.month_number\r\n"
                    + "                      AND o.status = 'Delivered'\r\n"
                    + "GROUP BY m.month_number;"
    )
    List<Integer> getMonthlyTotal();


    @Query(nativeQuery = true, value =
            "WITH AllQuarters AS (\r\n"
                    + "  SELECT 1 AS quarter_number UNION SELECT 2 UNION SELECT 3 UNION SELECT 4\r\n"
                    + ")\r\n"
                    + "SELECT  IFNULL(SUM(CASE WHEN o.currency = \"VNĐ\" THEN o.total\r\n"
                    + "                                      WHEN o.currency = \"USD\" THEN o.total * 24390.243902\r\n"
                    + "                                 END), 0) AS total_sum\r\n"
                    + "FROM AllQuarters q\r\n"
                    + "LEFT JOIN wck.orders o ON QUARTER(STR_TO_DATE(o.date, '%d/%m/%Y %H:%i:%s')) = q.quarter_number\r\n"
                    + "                    AND o.status = 'Delivered'\r\n"
                    + "GROUP BY q.quarter_number;"
    )
    List<Integer> getQuarterTotal();
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countOrdersByStatus(@Param("status") String status);
    int countByStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findOrderByStatus(@Param("status") String status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status and o.shipmentCompany.shipCid = :shipId")
    int countByStatusAndShip(@Param("status") String status,@Param("shipId") Long shipId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shipmentCompany.shipCid = :shipId")
    int countbyShipID(@Param("shipId") Long shipId);

    @Query("SELECT COUNT(o) FROM Order o")
    long countOrders();

    @Query(value = "SELECT IFNULL(SUM(CASE " +
            "WHEN currency = 'VNĐ' THEN total " +
            "WHEN currency = 'USD' THEN total * 24390.243902 " +
            "END), 0) AS total_month " +
            "FROM orders " +
            "WHERE MONTH(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = :month " +
            "AND YEAR(STR_TO_DATE(date, '%d/%m/%Y %H:%i:%s')) = YEAR(CURRENT_DATE()) " +
            "AND status = 'Delivered';", nativeQuery = true)
    long getRevenueForMonth(@Param("month") int month);

    @Query(value = "SELECT COUNT(*) FROM orders o WHERE o.status = :status " +
            "AND MONTH(STR_TO_DATE(o.date, '%d/%m/%Y %H:%i:%s')) = :month " +
            "AND YEAR(STR_TO_DATE(o.date, '%d/%m/%Y %H:%i:%s')) = YEAR(CURRENT_DATE())",
            nativeQuery = true)
    long getOrderCountByStatus(@Param("status") String status, @Param("month") int month);

    @Modifying  // Cần @Modifying để chỉ rõ đây là lệnh UPDATE/DELETE
    @Transactional // Cần @Transactional để đảm bảo lệnh này nằm trong transaction
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderId = :orderId")
    int updateStatus(@Param("orderId") Long orderId, @Param("status") String status);

}