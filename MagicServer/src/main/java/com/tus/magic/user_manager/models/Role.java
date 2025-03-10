package com.tus.magic.user_manager.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER("User"),
    SYSTEM_ADMIN("System Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String getAuthority() {
    	return name(); 
    }
}