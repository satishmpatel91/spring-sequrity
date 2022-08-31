package com.example.springsequrity.services;

import com.example.springsequrity.enitity.User;
import com.example.springsequrity.enitity.VerificationToken;
import com.example.springsequrity.model.UserModel;

import java.util.Optional;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    boolean verifyToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetToken(User user, String token);

    boolean validatePasswordVerifyToken(String token);

    Optional<User> getUserByToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfOldPasswordIsValid(User user, String oldPassword);

}
