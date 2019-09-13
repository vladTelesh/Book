package com.effectivesoft.bookservice.common.dto;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class PasswordsDto {
    @NotEmpty
    @Size(min = 6)
    private String currentPassword;
    @NotEmpty
    @Size(min = 6)
    private String newPassword;
    @NotEmpty
    @Size(min = 6)
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
