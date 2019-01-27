package com.javaman.service.impl;

import com.javaman.entity.PasswordResetToken;
import com.javaman.repository.PasswordResetTokenRepository;
import com.javaman.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService{

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public void delete(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }
}
