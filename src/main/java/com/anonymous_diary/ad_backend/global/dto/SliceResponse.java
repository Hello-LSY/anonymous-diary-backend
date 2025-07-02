package com.anonymous_diary.ad_backend.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class SliceResponse<T> {
    private final List<T> content;
    private final boolean hasNext;
    private final int page;
    private final int size;
}
