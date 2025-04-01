package klee.msvc.oauth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String username;

    private String password;

    private boolean enabled = true;

    private String email;

    private boolean admin = false;

    private List<Role> roles;
}
