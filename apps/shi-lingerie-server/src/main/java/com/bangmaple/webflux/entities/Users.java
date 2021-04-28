package com.bangmaple.webflux.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @Column("id")
    private int id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("fullname")
    private String fullname;

    @Column("role")
    private String role;

   // @Override
  //  public String getId() {
  //      return this.username;
  //  }
}
