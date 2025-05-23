package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.Roles;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    // Lấy tất cả các roles
    List<Roles> findAll();

    // Lấy danh sách roles theo danh sách ID
    Set<Roles> findByIds(List<Integer> ids);
}
