package com.example.springsequrity.model;

import lombok.Data;
@Data
public class UserModel {
    private String email;
    private String password;
    private String confirmPassword;
}
