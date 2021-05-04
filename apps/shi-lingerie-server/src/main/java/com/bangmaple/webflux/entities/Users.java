package com.bangmaple.webflux.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotBlank;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    public Users(String username, String password, String fullname) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = "ADMIN";
    }

    @Id
    @Column("id")
    private int id;

    @Column("username")
    @NotBlank(message = "Name is mandatory")
    private String username;

    @Column("password")
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column("fullname")
    @NotBlank(message = "Fullname is mandatory")
    private String fullname;

    @Column("role_name")
    @NotBlank(message = "Role is mandatory")
    private String role;

    @Column("is_activated")
    private boolean isActivated;

    public Users(Users user) {
    }

    // @Override
  //  public String getId() {
  //      return this.username;
  //  }
}
