package com.fof.demo.dto;

import com.fof.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Pour:
// toString
// equals
// hashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private  Long id;

    @NotBlank(message = "Champ Obligatoire")
    @Email
    private String email;

    @NotBlank(message = "Champ Obligatoire")
    private String lastName;

    @NotBlank(message = "Champ Obligatoire")
    private  String firstName;

    @Min(0)
    private int age;

    @NotBlank(message = "Champ Obligatoire")
    @Size(min = 6, max = 20)
    private String phone;

    private Role role;

}
