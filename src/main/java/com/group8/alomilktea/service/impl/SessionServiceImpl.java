package com.group8.alomilktea.service.impl;

import com.group8.alomilktea.entity.Session;
import com.group8.alomilktea.repository.SessionRepository;
import com.group8.alomilktea.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionServiceImpl implements ISessionService {
    @Autowired
    SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
    @Override
    public <S extends Session> S save(S entity) {
        return sessionRepository.save(entity);
    }

    @Override
    public List<Session> findAllSortedByDate() {
        return sessionRepository.findAllSortedByDate();
    }
}
