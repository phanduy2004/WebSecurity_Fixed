package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.ShipmentCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentCompanyRepository extends JpaRepository<ShipmentCompany, Integer> {
}
