package com.news.service;

//import com.news.payload.UserRegisterRequest;
import com.news.entity.User;
import com.news.exception.ResourceNotFoundException;
import com.news.payload.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
public interface UserService {

    RegisterResponse registerUser(UserDTO user);

    AuthResponse authenticateUser(AuthRequest request) throws AuthenticationException;

    void updateUser(@AuthenticationPrincipal UserDetails userDetails, UserUpdateRequest user);

    void changePassword(@AuthenticationPrincipal UserDetails userDetails, ChangePasswordRequest changePasswordRequest);


    User getByUsername(String username);

    Optional<User> getById(Long id);

    void updateUserAdmin(Long id,@AuthenticationPrincipal UserUpdateRequest userUpdateRequest);

    void changeUserStatus(Long userId,String status) throws ResourceNotFoundException;
}
