package com.news.service.impl;

import com.news.entity.Role;
import com.news.entity.User;
import com.news.entity.UserStatus;
import com.news.exception.*;
import com.news.payload.*;
import com.news.repository.RoleRepository;
import com.news.repository.UserRepository;
import com.news.security.JwtTokenUtil;
import com.news.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final JwtTokenUtil jwtUtil;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @Override
    public RegisterResponse registerUser(UserDTO userDTO) {

        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        String password = userDTO.getPassword();
        String cPassword = userDTO.getPasswordConfirmation();
        if (!isValidPassword(password) && !cPassword.equals(password)) {
            throw new InvalidPasswordException("Invalid password format");
        }
        // Ελέγξτε το όνομα χρήστη
        String username = userDTO.getUsername();
        if (!isValidUsername(username)) {
            throw new InvalidUserNameException("Invalid username format");
        }
        if(roleRepository.findByName(userDTO.getRoleName()) == null) {
            throw new ResourceNotFoundException("Invalid Role","role",userDTO.getUserId());
        }
        User user  = mapToEntity(userDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        // Map the RoleDTO to a Role entity
        Role role = roleRepository.findByName(userDTO.getRoleName());
        user.setRole(role);
        user.setUsername(username);
        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        user.setStatus(UserStatus.INACTIVE);
        User savedUser = userRepository.save(user);
        RegisterResponse registerResponse = modelMapper.map(mapToDTO(savedUser), RegisterResponse.class);
        return registerResponse;
    }

    @Transactional
    @Override
    public AuthResponse authenticateUser(AuthRequest request) throws AuthenticationException {
        try {
            User user = userRepository.findByUsername(request.getUsername());

            if (user != null && user.getStatus() == UserStatus.INACTIVE) {
                throw new UserInactiveException("User is inactive");
            }
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
            user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            // Invalidate the user's old token if they have one
            jwtUtil.invalidateUserToken(user.getUsername());
            resetFailedLoginAttempts(user);
            return new AuthResponse(user.getUsername(), accessToken);

        } catch (BadCredentialsException ex) {
            User user = userRepository.findByUsername(request.getUsername());
            if (user != null) {
                incrementFailedLoginAttempts(user);
                if (user.getFailedLoginAttempts() >= 3) {
                    // Set the user as inactive
                    user.setStatus(UserStatus.INACTIVE);
                    userRepository.save(user);
                    // Log or notify about the user being deactivated
                }
            }
            throw new AuthenticationException("Authentication failed");
        }
    }


    private void resetFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    private void incrementFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void updateUser(UserDetails userDetails, UserUpdateRequest userUpdateRequest) {
        String currentUsername = userDetails.getUsername();
        User user = userRepository.findByUsername(currentUsername);

        if (user != null) {
            if (!userUpdateRequest.getUsername().equals(currentUsername)) {
                jwtUtil.invalidateUserToken(currentUsername);
            }
            user.setUsername(userUpdateRequest.getUsername());
            user.setFirstname(userUpdateRequest.getFirstname());
            user.setLastname(userUpdateRequest.getLastname());
            String roleName = userUpdateRequest.getRolename();
            Role role = roleRepository.findByName(roleName);
            if(role.getName().equals(null)){
                throw new RoleNotFoundException("Role dont exist");
            }
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found with id: ","user",user.getId());
        }
    }

    @Transactional
    @Override
    public void updateUserAdmin(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id).orElseThrow(null);

        if (user != null) {
            if (!userUpdateRequest.getUsername().equals(user.getUsername())) {
                jwtUtil.invalidateUserToken(user.getUsername());
            }
            user.setUsername(userUpdateRequest.getUsername());
            user.setFirstname(userUpdateRequest.getFirstname());
            user.setLastname(userUpdateRequest.getLastname());
            String roleName = userUpdateRequest.getRolename();
            Role role = roleRepository.findByName(roleName);
            if(role.getName().equals(null)){
                throw new RoleNotFoundException("Role dont exist");
            }
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User not found with id: ","user",user.getId());
        }
    }

    @Transactional
    @Override
    public void changeUserStatus(Long userId, String status) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: ","user",userId));

        // Check the 'status' parameter and update the user's status accordingly
        if (status.equals("active")) {
            user.setStatus(UserStatus.ACTIVE);
        } else if (status.equals("inactive")) {
            user.setStatus(UserStatus.INACTIVE);
            // Cancel the user's tokens (invalidate tokens) if setting to 'inactive'
            jwtUtil.invalidateUserToken(user.getUsername());
        } else {
            throw new UserInactiveException("The status you provided does not exists");
        }
        userRepository.save(user);
    }

    @Override
    public void changePasswordByAdmin(Long userId, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: ","user:", userId));
        String newPassword = changePasswordRequest.getNewPassword();
        String newPasswordConfirmation = changePasswordRequest.getNewPasswordConfirmation();

        if (!newPassword.equals(newPasswordConfirmation)) {
            throw new PasswordConfirmationException("New password and confirmation do not match");
        }
        user.setPassword(encoder.encode(newPassword));
        jwtUtil.invalidateUserToken(user.getUsername());
        userRepository.save(user);
    }

    @Override
    public void changePasswordByUser(UserDetails userDetails, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new InvalidUserNameException("User not found");
        }
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String newPasswordConfirmation = changePasswordRequest.getNewPasswordConfirmation();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            // Passwords do not match; increment failed attempts counter
            incrementFailedPasswordChangeAttempts(user);
            if (user.getFailedPasswordChangeAttempts() >= 3) {
                // Set the user as inactive if three consecutive failures
                user.setStatus(UserStatus.INACTIVE);
            }
            throw new InvalidPasswordException("Old password is incorrect");
        }
        // Check if the new password and confirmation match
        if (!newPassword.equals(newPasswordConfirmation)) {
            throw new PasswordConfirmationException("New password and confirmation do not match");
        }
        // Reset failed password change attempts on successful password change
        resetFailedPasswordChangeAttempts(user);
        // Update the user's password
        user.setPassword(encoder.encode(newPassword));
        // Invalidate the user's current token
        jwtUtil.invalidateUserToken(userDetails.getUsername());
        // Save the updated user
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId, UserDetails userDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: ","user " , userId));

        if (!userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
                && !userDetails.getUsername().equals(user.getUsername())) {
            throw new UnauthorizedException("You are not authorized to delete this user.");
        }
        jwtUtil.invalidateUserToken(user.getUsername());
        userRepository.delete(user);
    }

    private void incrementFailedPasswordChangeAttempts(User user) {
        user.setFailedPasswordChangeAttempts(user.getFailedPasswordChangeAttempts() + 1);
        userRepository.save(user);
    }

    // Helper method to reset failed password change attempts
    private void resetFailedPasswordChangeAttempts(User user) {
        user.setFailedPasswordChangeAttempts(0);
        userRepository.save(user);
    }

        private boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).*$");
    }

    private boolean isValidUsername(String username) {
        return username.length() >= 5 && username.matches("^[a-zA-Z_]\\w*$");
    }

    private UserDTO mapToDTO(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    private User mapToEntity(UserDTO userDTO){
        User user = modelMapper.map(userDTO, User.class);
        return user;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

}

