package com.group8.alomilktea.repository;

import com.group8.alomilktea.entity.Session;
import com.group8.alomilktea.entity.SessionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, SessionKey> {
    @Query("SELECT s FROM session s ORDER BY s.date DESC")
    List<Session> findAllSortedByDate();
}
