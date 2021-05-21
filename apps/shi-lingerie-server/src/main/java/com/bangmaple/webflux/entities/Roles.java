package com.bangmaple.webflux.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Roles {
    @Id
    @Column("id")
    @NonNull
    private Integer id;

    @Column("role_name")
    private String roleName;


}
