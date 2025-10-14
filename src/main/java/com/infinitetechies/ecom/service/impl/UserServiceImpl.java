package com.infinitetechies.ecom.service.impl;

import com.infinitetechies.ecom.exception.ResourceNotFoundException;
import com.infinitetechies.ecom.model.Product;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.dto.request.UserUpdateRequest;
import com.infinitetechies.ecom.model.dto.response.UserResponse;
import com.infinitetechies.ecom.repository.UserRepository;
import com.infinitetechies.ecom.service.inf.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    public UserResponse getUser(long userId) {
        return mapToUserResponse(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

