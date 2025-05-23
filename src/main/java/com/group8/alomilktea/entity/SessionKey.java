package com.group8.alomilktea.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;
@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionKey{

    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    Integer user_id;

    @Column(name = "pro_id")
    Integer pro_id;

    // standard constructors, getters, and setters
    // hashcode and equals implementation

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionKey that = (SessionKey) o;
        return Objects.equals(user_id, that.user_id) &&
                Objects.equals(pro_id, that.pro_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, pro_id);
    }
}
