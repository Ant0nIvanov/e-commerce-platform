package ru.ivanov.productservice.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        List<T> content
) {
    public static <T> PagedResponse<T> fromPage(Page<T> page) {
        return new PagedResponse<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.getContent()
        );
    }
}