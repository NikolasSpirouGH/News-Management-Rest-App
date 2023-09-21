package com.news.service;

//import com.news.payload.UserRegisterRequest;
import com.news.entity.User;
import com.news.exception.ResourceNotFoundException;
import com.news.payload.*;
import jakarta.validation.Valid;
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
    User getByUsername(String username);

    Optional<User> getById(Long id);

    void updateUserAdmin(Long id,@AuthenticationPrincipal UserUpdateRequest userUpdateRequest);

    void changeUserStatus(Long userId,String status) throws ResourceNotFoundException;

    void changePasswordByAdmin(Long id, ChangePasswordRequest userUpdateRequest);

    void changePasswordByUser(UserDetails userDetails, ChangePasswordRequest changePasswordRequest);

    void deleteUser(Long userId, UserDetails userDetails);
}
