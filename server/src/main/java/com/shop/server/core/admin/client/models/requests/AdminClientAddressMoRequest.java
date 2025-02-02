package com.shop.server.core.admin.client.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminClientAddressMoRequest {

    private String name;

    private String phoneNumber;

    private String line;

    private String ward;

    private Long district;

    private Long province;

    private String email;

    private String code;

    private String birthday;

    private Boolean gender;

    private String picture;

}
