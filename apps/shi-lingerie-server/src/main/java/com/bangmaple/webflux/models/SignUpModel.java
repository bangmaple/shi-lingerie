package com.bangmaple.webflux.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpModel {
    private String username;
    private String password;
    private String fullname;
}
