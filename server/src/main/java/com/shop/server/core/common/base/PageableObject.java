package com.shop.server.core.common.base;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageableObject<T> {

    private final List<T> data;

    private final long totalPages;

    private final int currentPage;

    private final long totalElements;

    public PageableObject(Page<T> page) {
        this.data = page.getContent();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber();
        this.totalElements = page.getTotalElements();
    }

    public static <T> PageableObject<T> of(Page<T> page) {
        return new PageableObject<>(page);
    }

}
