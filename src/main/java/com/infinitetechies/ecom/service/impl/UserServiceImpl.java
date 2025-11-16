package com.infinitetechies.ecom.service.impl;

import com.infinitetechies.ecom.exception.ResourceNotFoundException;
import com.infinitetechies.ecom.exception.UnauthorizedException;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.UserPrincipal;
import com.infinitetechies.ecom.model.dto.response.UserResponse;
import com.infinitetechies.ecom.repository.UserRepository;
import com.infinitetechies.ecom.service.inf.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        return mapToUserResponse(userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with Id: "+id)));
    }

    @Override
    public User getCurrentUserById(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    private UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with email"));
        return new UserPrincipal(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return List.of();
    }

    @Override
    public User getUserById(long userId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @Override
    public UserResponse getUser(long userId) {
        return null;
    }
}

