package com.group8.alomilktea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "session")
public class Session {
    @EmbeddedId
    private SessionKey id;

    @ManyToOne
    @MapsId("pro_id")
    @JoinColumn(name = "pro_id")
    private Product product;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "date")
    private Date date;

}
