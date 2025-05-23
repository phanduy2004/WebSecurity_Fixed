package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.ShipmentCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IShipmentCompany {
    public List<ShipmentCompany> findAll();
    ShipmentCompany save(ShipmentCompany shipmentCompany);

    ShipmentCompany findById(Integer shipCid);

    void deleteById(Integer shipCid);

    Page<ShipmentCompany> getAll(Pageable pageable);
}
