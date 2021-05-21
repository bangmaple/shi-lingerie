package com.bangmaple.webflux.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    @Column("id")
    private Integer id;

    @Column("username")
    private String username;

    @Column("fullname")
    private String fullname;

    @Column("is_activated")
    private Boolean isActivated;

    @Column("role_name")
    private String role;

}
