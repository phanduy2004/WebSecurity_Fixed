package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.OrderDetail;

public interface IOrderDetailService {
    <S extends OrderDetail> S save(S entity);
}
