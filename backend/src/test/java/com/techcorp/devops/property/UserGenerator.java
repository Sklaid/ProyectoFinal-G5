package com.techcorp.devops.property;

import com.techcorp.devops.entity.Role;
import com.techcorp.devops.entity.User;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;

import java.time.LocalDateTime;

/**
 * Generator for creating random User entities for property-based testing.
 * 
 * This generator creates valid User objects with randomized data suitable for testing.
 * All generated users have:
 * - Valid usernames (5-20 lowercase letters)
 * - Valid passwords (8-30 alphanumeric characters)
 * - Valid email addresses
 * - Random roles (USER or ADMIN)
 * - Active status set to true
 * - Timestamps set to current time
 */
public class UserGenerator {
    
    /**
     * Generates valid User entities with random data.
     * 
     * @return Arbitrary that produces random User objects
     */
    public static Arbitrary<User> validUsers() {
        Arbitrary<String> usernames = Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(5)
                .ofMaxLength(20);
        
        Arbitrary<String> passwords = Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(8)
                .ofMaxLength(30);
        
        Arbitrary<String> emails = usernames.map(name -> name + "@example.com");
        
        Arbitrary<Role> roles = Arbitraries.of(Role.class);
        
        Arbitrary<Boolean> activeStatus = Arbitraries.of(true, false);
        
        Arbitrary<LocalDateTime> timestamps = Arbitraries.integers()
                .between(0, 365) // Last year
                .map(days -> LocalDateTime.now().minusDays(days));
        
        return Combinators.combine(
                usernames,
                passwords,
                emails,
                roles,
                activeStatus,
                timestamps
        ).as((username, password, email, role, active, createdAt) ->
                User.builder()
                        .username(username)
                        .password(password) // Note: In real tests, this should be encoded
                        .email(email)
                        .role(role)
                        .active(active)
                        .createdAt(createdAt)
                        .build()
        );
    }
    
    /**
     * Generates User entities with active status set to true.
     * 
     * @return Arbitrary that produces random active User objects
     */
    public static Arbitrary<User> activeUsers() {
        return validUsers().map(user -> {
            user.setActive(true);
            return user;
        });
    }
    
    /**
     * Generates User entities with ADMIN role.
     * 
     * @return Arbitrary that produces random admin User objects
     */
    public static Arbitrary<User> adminUsers() {
        return validUsers().map(user -> {
            user.setRole(Role.ADMIN);
            return user;
        });
    }
    
    /**
     * Generates User entities with USER role.
     * 
     * @return Arbitrary that produces random regular User objects
     */
    public static Arbitrary<User> regularUsers() {
        return validUsers().map(user -> {
            user.setRole(Role.USER);
            return user;
        });
    }
}
