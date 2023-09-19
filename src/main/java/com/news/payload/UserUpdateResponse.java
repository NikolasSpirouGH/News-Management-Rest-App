package com.news.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateResponse {

    private String message;
    private String username;
    private String firstname;
    private String lastname;
    private String role;
}
