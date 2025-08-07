package com.example.parastoreb.auth;

import com.example.parastoreb.dto.auth.RegisterRequest;
import com.example.parastoreb.dto.auth.LoginRequest;
import com.example.parastoreb.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
