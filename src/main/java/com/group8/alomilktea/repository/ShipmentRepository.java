package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.ShipmentCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentCompany, Integer> {
}
