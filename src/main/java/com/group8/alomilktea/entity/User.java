package com.group8.alomilktea.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.group8.alomilktea.common.enums.Status;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user") // �?m b?o t�n b?ng l� "users"
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "password", length = 255)
    private String password;

    private Status status;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "email", length = 255, unique = true, nullable = true)
    private String email;

    @Column(name = "phone", length = 255, unique = false, nullable = true)
    private String phone;

    @Column(name = "address", length = 1000)
    private String address;

    @Column(name = "is_admin", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isAdmin;

    @Column(name = "active", columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean active;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "password_salt", length = 255)
    private String passwordSalt;

    @Column(name = "username", length = 255, unique = true)
    private String username;

    @Column(name = "code", length = 255)
    private String code;

    @Column(name = "is_enabled", columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean isEnabled;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ShipmentCompany shipmentCompany;
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", isAdmin=" + isAdmin +
                ", active=" + active +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
