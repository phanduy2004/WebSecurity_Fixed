package com.group8.alomilktea.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpModel {
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String username;
}
