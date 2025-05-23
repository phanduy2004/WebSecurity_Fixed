package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.Roles;
import com.group8.alomilktea.repository.RoleRepository;
import com.group8.alomilktea.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service

public class IRoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository repo;
    @Override
    public List<Roles> findAll() {
        return repo.findAll();
    }

    @Override
    public Set<Roles> findByIds(List<Integer> ids) {
        return repo.findByIdIn(ids);
    }
}
