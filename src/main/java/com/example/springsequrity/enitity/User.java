package com.example.springsequrity.enitity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    @Column(length = 60)
    private String password;
    private String role;
    private boolean enable=false;
}
