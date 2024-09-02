package com.example.usermanagementsystem.service;

import com.example.usermanagementsystem.exception.UserNotFoundException;
import com.example.usermanagementsystem.model.User;
import com.example.usermanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // Read
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Update
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        return userRepository.save(user);
    }

    // Delete
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
