package com.crawler.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "회원"),
    GUEST("ROLE_GUEST", "비회원"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}
