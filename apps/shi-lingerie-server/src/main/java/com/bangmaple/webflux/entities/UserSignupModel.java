package com.bangmaple.webflux.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupModel {
    private String username;
    private String password;
    private String fullname;
}
