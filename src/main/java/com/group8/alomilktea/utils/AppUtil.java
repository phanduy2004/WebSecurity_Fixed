package com.group8.alomilktea.utils;

import com.group8.alomilktea.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

@Component
public class AppUtil {

    @Autowired
    PasswordEncoder passwordEncoder;

    public static final Random RANDOM = new SecureRandom();
    public static final String UTF8_BOM = "\uFEFF";

    public  String generateSalt() {
        byte[] salt = new byte[Constant.SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public boolean checkPassword(String passwordHash, User user) {
        return passwordEncoder.matches(
                passwordHash.trim().concat(user.getPasswordSalt()), user.getPasswordHash());
    }
    public String generatePasswordHash(String passwordHash, User user) {
        return passwordEncoder.encode(passwordHash.trim().concat(user.getPasswordSalt()));
    }

}

