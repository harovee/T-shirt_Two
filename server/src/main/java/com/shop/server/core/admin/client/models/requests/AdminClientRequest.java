package com.shop.server.core.admin.client.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminClientRequest {

    private String password;

    private String name;

    private String email;

    private String code;

    private String birthday;

    private Boolean gender;

    private String phoneNumber;

    private String picture;

}
