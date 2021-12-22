package ru.netcracker.backend.util.component;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    private SecurityUtil() {

    }

    public static String getUsernameFromSecurityCtx() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
