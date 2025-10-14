package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.dto.request.LoginRequest;
import com.infinitetechies.ecom.model.dto.request.RegisterRequest;
import com.infinitetechies.ecom.model.dto.response.AuthResponse;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
