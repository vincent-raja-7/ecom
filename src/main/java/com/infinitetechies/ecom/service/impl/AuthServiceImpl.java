package com.infinitetechies.ecom.service.impl;
import com.infinitetechies.ecom.exception.BadRequestException;
import com.infinitetechies.ecom.model.Cart;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.Wishlist;
import com.infinitetechies.ecom.model.dto.request.LoginRequest;
import com.infinitetechies.ecom.model.dto.request.RegisterRequest;
import com.infinitetechies.ecom.model.dto.response.AuthResponse;
import com.infinitetechies.ecom.repository.CartRepository;
import com.infinitetechies.ecom.repository.UserRepository;
import com.infinitetechies.ecom.repository.WishlistRepository;
import com.infinitetechies.ecom.service.inf.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;

    // Password encoding/validation can be added here in the future if needed

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists!");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword()) // plain text for demo; ALWAY encrypt in production
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        // Create cart and wishlist for the new user
        Cart cart = Cart.builder()
                .user(savedUser)
                .build();
        cartRepository.save(cart);

        Wishlist wishlist = Wishlist.builder()
                .user(savedUser)
                .build();
        wishlistRepository.save(wishlist);

        // Return simple user info (no JWT/token)
        return AuthResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .type("Basic") // Just an indicator
                .token(null)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // For demo: check plain text password match (do NOT use in real apps)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .type("Basic")
                .token(null)
                .build();
    }
}

