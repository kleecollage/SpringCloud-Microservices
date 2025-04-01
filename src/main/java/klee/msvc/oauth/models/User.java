package klee.msvc.oauth.models;

import lombok.Data;

import java.util.List;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    private boolean enabled = true;

    private String email;

    private boolean admin = false;

    private List<Role> roles;
}
