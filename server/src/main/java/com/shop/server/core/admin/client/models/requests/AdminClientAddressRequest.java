package com.shop.server.core.admin.client.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminClientAddressRequest {

    private String name;

    private String phoneNumber;

    private String line;

    private String ward;

    private Long district;

    private Long province;

    private String clientId;

}
