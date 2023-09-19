package com.news.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleDTO {

    private Long id;

    @Pattern(regexp = "ROLE_(VISITOR|JOURNALIST|EDITOR|ADMIN)", message = "Invalid role name")
    private String name;
}
