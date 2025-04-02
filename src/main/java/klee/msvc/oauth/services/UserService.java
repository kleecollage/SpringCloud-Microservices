package klee.msvc.oauth.services;

import klee.msvc.oauth.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final WebClient client;

    public UserService(WebClient client) {
        this.client = client;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Enter to login process UserService::loadUserByUsername with {}", username);
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        try {
            User user = client.get().uri("api/users/username/{username}", params)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();

            if (user == null)
                throw new UsernameNotFoundException("User Not Found");

            List<GrantedAuthority> roles = user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
            logger.info("User has successfully logged by username: {}", user );
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(),
                    true,
                    true,
                    true,
                    roles);
        } catch (WebClientResponseException e) {
            String error = "Error: user with name: " + username + " not exists" + "\n" + e.getMessage();
            logger.error(error);
            throw new UsernameNotFoundException(error);
        }
    }
}
