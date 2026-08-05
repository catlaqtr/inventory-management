package com.example.inventory_management.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CompanyContext {

    private CompanyContext() {
    }

    public static CompanyUserDetails requireCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CompanyUserDetails details)) {
            throw new IllegalStateException("Authenticated company user required.");
        }

        return details;
    }

    public static Long requireCompanyId() {
        return requireCurrentUser().getCompanyId();
    }

    public static boolean isAdmin() {
        return "ROLE_ADMIN".equals(requireCurrentUser().getRole());
    }
}
