package com.infinitetechies.ecom.service.impl;
import com.infinitetechies.ecom.exception.BadRequestException;
import com.infinitetechies.ecom.model.Cart;
import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.Wishlist;
import com.infinitetechies.ecom.model.dto.request.LoginRequest;
import com.infinitetechies.ecom.model.dto.request.RegisterRequest;
import com.infinitetechies.ecom.model.dto.response.AuthResponse;
import com.infinitetechies.ecom.model.enums.UserRole;
import com.infinitetechies.ecom.repository.CartRepository;
import com.infinitetechies.ecom.repository.UserRepository;
import com.infinitetechies.ecom.repository.WishlistRepository;
import com.infinitetechies.ecom.service.inf.IAuthService;
import com.infinitetechies.ecom.service.inf.IJwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Email already exists!");

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        if(request.getRole().equals(UserRole.USER.toString())){
            Cart cart = Cart.builder()
                    .user(savedUser)
                    .build();
            cartRepository.save(cart);

            Wishlist wishlist = Wishlist.builder()
                    .user(savedUser)
                    .build();
            wishlistRepository.save(wishlist);
        }
        return "User Created Successfully!";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(()-> new BadRequestException("Invalid Email or Password!"));
//
//        if(!user.getPassword().equals(request.getPassword()))
//            throw new BadRequestException("Invalid Email or Password!");
//
//        return AuthResponse.builder()
//                .token("123")
//                .build();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));

        if(authentication.isAuthenticated()) {
            try{
                return AuthResponse.builder()
                        .token(jwtService.generateToken(request.getEmail()))
                        .build();
            }
            catch (NoSuchAlgorithmException ex){
                throw new RuntimeException("Error generating secret key", ex);
            }
        }
        return AuthResponse.builder()
                .token("")
                .build();
    }
}