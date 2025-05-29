package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.Promotion;
import com.group8.alomilktea.repository.PromotionRepository;
import com.group8.alomilktea.service.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements IPromotionService {
    @Autowired
    private PromotionRepository repo;
    @Override
    public List<Promotion> findAll() {
        return repo.findAll();
    }

    @Override
    public Promotion findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public void save(Promotion promotion) {
        repo.save(promotion);
    }

    @Override
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public Page<Promotion> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    @Override
    public Optional<Promotion> findByName(String name) {
        return repo.findByName(name);
    }
}
