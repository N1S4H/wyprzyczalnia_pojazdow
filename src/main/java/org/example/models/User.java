package org.example.models;

import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.function.Function;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private static final long serialVersionUID = 1L;
    private String id;
    private String login;
    private String passwordHash;
    private Role role;


    public User copy(){
        return User.builder().id(id).login(login).passwordHash(passwordHash).role(role).build();

    }


}