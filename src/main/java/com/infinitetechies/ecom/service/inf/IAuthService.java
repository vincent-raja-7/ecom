package com.infinitetechies.ecom.service.inf;

import com.infinitetechies.ecom.model.dto.request.LoginRequest;
import com.infinitetechies.ecom.model.dto.request.RegisterRequest;
import com.infinitetechies.ecom.model.dto.response.AuthResponse;
import com.infinitetechies.ecom.model.dto.response.AuthResponseV2;

public interface IAuthService {
    String register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponseV2 loginV2(LoginRequest request);
    AuthResponseV2 refreshToken(String refreshToken);
}
