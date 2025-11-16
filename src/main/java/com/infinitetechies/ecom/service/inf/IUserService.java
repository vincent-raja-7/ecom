package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.dto.request.UserUpdateRequest;
import com.infinitetechies.ecom.model.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface IUserService extends UserDetailsService {
    List<UserResponse> getUsers();

    UserResponse getUserById(Long id);

    User getCurrentUserById(Long id);

    //    UserResponse getCurrentUser();
//    UserResponse updateCurrentUser(UserUpdateRequest request);
    List<UserResponse> getAllUsers();
    User getUserById(long userId);
    UserResponse getUser(long userId);
//    User getCurrentUserEntity();
}
