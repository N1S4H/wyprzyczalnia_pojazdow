package org.example.models;

import lombok.*;
import jakarta.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.function.Function;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "passwordHash")
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(name = "password", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    public User copy(){
        return User.builder()
                .id(id).
                login(login)
                .passwordHash(passwordHash)
                .role(role)
                .build();
    }
}