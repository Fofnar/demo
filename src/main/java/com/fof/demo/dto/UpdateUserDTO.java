package com.fof.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

    @Email
    private String email;

    @Size(min = 2)
    private String lastName;

    @Size(min = 2)
    private String firstName;

    @Min(0)
    private int age;

    @Size(min = 6, max = 20)
    private String phone;

    @Size(min = 8)
    private String password;
}
