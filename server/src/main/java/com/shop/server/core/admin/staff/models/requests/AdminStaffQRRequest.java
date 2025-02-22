package com.shop.server.core.admin.staff.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminStaffQRRequest {

    String name;

    String birthday;

    String gender;

    String identity;

}
