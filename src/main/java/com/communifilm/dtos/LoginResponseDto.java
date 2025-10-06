package com.communifilm.dtos;

import com.communifilm.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseDto {
    private boolean isNewUser;
    private User user;

    public LoginResponseDto(boolean isNewUser, User user) {
        this.isNewUser = isNewUser;
        this.user = user;
    }
    
    @JsonProperty("isNewUser")
    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}