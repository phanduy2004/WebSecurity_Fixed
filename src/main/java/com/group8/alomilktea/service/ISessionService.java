package com.group8.alomilktea.service;

import com.group8.alomilktea.entity.Session;

import java.util.List;

public interface ISessionService {
    <S extends Session> S save(S entity);
    List<Session> findAllSortedByDate();
}
