package com.moneymanager.util;

import com.moneymanager.config.AuthenticatedUser;
import com.moneymanager.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static AuthenticatedUser requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof AuthenticatedUser)) {
            throw new ForbiddenException("Unauthorized");
        }
        return (AuthenticatedUser) auth.getPrincipal();
    }

    public static String requireUserId() {
        AuthenticatedUser u = requireUser();
        if (u.getId() == null || u.getId().isBlank()) {
            throw new ForbiddenException("Unauthorized");
        }
        return u.getId();
    }

    public static String requireEmail() {
        AuthenticatedUser u = requireUser();
        return u.getEmail();
    }
}
