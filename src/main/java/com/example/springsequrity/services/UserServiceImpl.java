package com.example.springsequrity.services;

import com.example.springsequrity.enitity.PasswordResetToken;
import com.example.springsequrity.enitity.User;
import com.example.springsequrity.enitity.VerificationToken;
import com.example.springsequrity.model.UserModel;
import com.example.springsequrity.repository.PasswordResetTokenRepository;
import com.example.springsequrity.repository.UserRepository;
import com.example.springsequrity.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public boolean verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        Calendar calendar = Calendar.getInstance();

        if (verificationToken == null) {
            return false;
        }
        User user = verificationToken.getUser();
        if (verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return false;
        }
        user.setEnable(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);

        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public boolean validatePasswordVerifyToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        Calendar calendar = Calendar.getInstance();

        if (passwordResetToken == null) {
            return false;
        }
        User user = passwordResetToken.getUser();
        if (passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return false;
        }
        return true;
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfOldPasswordIsValid(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());

    }
}
