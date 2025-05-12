package net.enjoy.springboot.registrationlogin.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import net.enjoy.springboot.registrationlogin.entity.Role;
import net.enjoy.springboot.registrationlogin.entity.User;
import net.enjoy.springboot.registrationlogin.repository.RoleRepository;
import net.enjoy.springboot.registrationlogin.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register user with optional roles
    @PostMapping("/save")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        if (userRepository.findByName(user.getName()) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> assignedRoles = new HashSet<>();

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            // Assign default ROLE_USER if no roles provided
            Role defaultRole = roleRepository.findByName("ROLE_USER");
            if (defaultRole == null) {
                defaultRole = new Role();
                defaultRole.setName("ROLE_USER");
                defaultRole = roleRepository.save(defaultRole);
            }
            assignedRoles.add(defaultRole);
        } else {
            for (Role role : user.getRoles()) {
                String roleName = role.getName().toUpperCase();
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                Role existingRole = roleRepository.findByName(roleName);
                if (existingRole == null) {
                    existingRole = new Role();
                    existingRole.setName(roleName);
                    existingRole = roleRepository.save(existingRole);
                }
                assignedRoles.add(existingRole);
            }
        }

        user.setRoles(new ArrayList<>(assignedRoles));
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Dummy login endpoint (handled by Spring Security)
    @PostMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login successful");
    }
}
