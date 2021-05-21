package com.bangmaple.webflux.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddNewUserModel {
  @NotBlank(message = "Username is mandatory.")
  private String username;
  @NotBlank(message = "Password is mandatory.")
  private String password;
  @NotBlank(message = "Fullname is mandatory.")
  private String fullname;
  @NotBlank(message = "User activation is mandatory.")
  private Boolean isActivated;
}
