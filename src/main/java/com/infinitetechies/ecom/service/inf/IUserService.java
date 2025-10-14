package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.User;
import com.infinitetechies.ecom.model.dto.request.UserUpdateRequest;
import com.infinitetechies.ecom.model.dto.response.UserResponse;

import java.util.List;

public interface IUserService {
//    UserResponse getCurrentUser();
//    UserResponse updateCurrentUser(UserUpdateRequest request);
    List<UserResponse> getAllUsers();
    User getUserById(long userId);
    UserResponse getUser(long userId);
//    User getCurrentUserEntity();
}
