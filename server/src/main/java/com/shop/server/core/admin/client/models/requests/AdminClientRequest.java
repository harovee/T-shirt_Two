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

    String password;

    String name;

    String email;

    String code;

    String birthday;

    Boolean gender;

    String phoneNumber;

    String picture;

}
