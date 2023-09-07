package com.news.service;

import com.news.payload.AuthenticationRequest;
import com.news.payload.AuthenticationResponse;
import com.news.payload.UserRegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    AuthenticationResponse register(UserRegisterRequest userRegisterRequest);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
