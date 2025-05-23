package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.OrderDetail;
import com.group8.alomilktea.repository.OrderDetailRepository;
import com.group8.alomilktea.service.IOrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl implements IOrderDetailService {
    OrderDetailRepository repo;
    @Override
    public <S extends OrderDetail> S save(S entity) {
        return repo.save(entity);
    }

    public OrderDetailServiceImpl(OrderDetailRepository repo) {
        this.repo = repo;
    }
}
