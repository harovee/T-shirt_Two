package com.shop.server.infrastructure.constants.module;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Role {

    ADMIN,

    USER,

    CLIENT;

    public static List<String> Roles() {
        return Arrays.stream(Role.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

}
