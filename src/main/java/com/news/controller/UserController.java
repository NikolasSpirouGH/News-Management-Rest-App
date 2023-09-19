package com.news.controller;

import com.news.exception.ResourceNotFoundException;
import com.news.payload.*;
import com.news.security.JwtTokenUtil;
import com.news.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@Valid @RequestBody UserDTO userDTO) {
        RegisterResponse registeredUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) throws AuthenticationException {
        AuthResponse response = userService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISITOR') or hasAuthority('EDITOR') or hasAuthority('JOURNALIST')")
    @PutMapping("/updateSelfUser")
    public ResponseEntity<String> updateUser(
            @Valid @RequestBody UserUpdateRequest userUpdateRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateUser(userDetails,userUpdateRequest);
        return new ResponseEntity<>("User updated with usernmame" +" " + userDetails.getUsername(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/updateUserByAdmin")
    public ResponseEntity<String> updateUserByAdmin(
            @RequestParam Long id,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        userService.updateUserAdmin(id,userUpdateRequest);
        return new ResponseEntity<>("User updated with usernmame" +" " + userUpdateRequest.getUsername(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/changeStatus/{userId}/{status}")
    public ResponseEntity<String> changeUserStatus(@PathVariable Long userId,@PathVariable String status) {
        System.out.println("help");
        try {
            userService.changeUserStatus(userId,status);
            return new ResponseEntity<>("User status changed successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("User not found with id: " + userId, HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasΑuthority('ADMIN')")
    @PutMapping("/changeUserPassword/{userId}")
    public ResponseEntity<String> changeUserPasswordByAdmin(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        userService.changePasswordByAdmin(userId, changePasswordRequest);
        return new ResponseEntity<>("User password changed successfully.", HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISITOR') or hasAuthority('EDITOR') or hasAuthority('JOURNALIST')")
    @PutMapping("/changePasswordByUser")
    public ResponseEntity<String> changePasswordByUser(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChangePasswordRequest changePasswordRequest){
        userService.changePasswordByUser(userDetails,changePasswordRequest);
        return new ResponseEntity<>("Password changed.", HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('VISITOR') or hasAuthority('EDITOR') or hasAuthority('JOURNALIST')")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            userService.deleteUser(userId, userDetails);
            return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("User not found with id: " + userId, HttpStatus.NOT_FOUND);
        }
    }

}
