package ru.netcracker.backend.util;

import org.springframework.security.core.context.SecurityContextHolder;

final public class SecurityUtil {
    public static String getUsernameFromSecurityCtx() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
