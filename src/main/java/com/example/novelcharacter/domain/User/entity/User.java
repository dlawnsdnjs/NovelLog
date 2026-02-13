package com.example.novelcharacter.domain.User.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="uuid")
    private long uuid;

    @Column(name = "userId", unique = true, length = 60 ,nullable = false)
    private String userId;

    @Column(name = "userName", unique = true, length = 40 ,nullable = false)
    private String userName;

    @Column(name = "email", unique = true, length = 40)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "lastLoginDate", nullable = false)
    private LocalDate lastLoginDate;

    @Column(name = "privacy_agreed_at")
    private LocalDate privacy_agreed_at;
}
