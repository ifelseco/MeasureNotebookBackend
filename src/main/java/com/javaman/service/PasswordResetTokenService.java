package com.javaman.service;

import com.javaman.entity.PasswordResetToken;

public interface PasswordResetTokenService {

    PasswordResetToken findByToken(String token);
    PasswordResetToken save(PasswordResetToken passwordResetToken);
    void delete(PasswordResetToken passwordResetToken);


}
