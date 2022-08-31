package com.example.springsequrity.controller;

import com.example.springsequrity.enitity.User;
import com.example.springsequrity.enitity.VerificationToken;
import com.example.springsequrity.event.RegistrationCompleteEvent;
import com.example.springsequrity.event.TokenResendCompleteEvent;
import com.example.springsequrity.model.PasswordModel;
import com.example.springsequrity.model.UserModel;
import com.example.springsequrity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @PostMapping(path = "/register")
    public ResponseEntity<User> registerUser(@RequestBody UserModel userModel, final HttpServletRequest httpServletRequest) {
        User user = userService.registerUser(userModel);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(httpServletRequest)
        ));
        return ResponseEntity.ok(user);
    }

    @GetMapping(path = "/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        boolean isValid = userService.verifyToken(token);
        if (isValid) {
            return "Verification successfully completed";
        } else {
            return "Verification failed!!! Invalid token";
        }
    }

    @GetMapping(path = "/resendVerifyToken")
    public String resendVerifyToken(@RequestParam("token") String oldToken, final HttpServletRequest httpServletRequest) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);

        applicationEventPublisher.publishEvent(new TokenResendCompleteEvent(
                verificationToken,
                applicationUrl(httpServletRequest)
        ));

        //resendVerificationTokenMail(user, applicationUrl(httpServletRequest), verificationToken);
        return "Verification Link Sent";

    }


    @PostMapping(path = "/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest httpServletRequest) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetToken(user, token);
            url = passwordResetTokenMail(user, applicationUrl(httpServletRequest), token);
        }
        return "Click Link to reset password " + url;
    }

    @PostMapping(path = "/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user=userService.findUserByEmail(passwordModel.getEmail());
       if(!userService.checkIfOldPasswordIsValid(user,passwordModel.getOldPassword()))
       {
            return "Invalid Old Password";
       }
       userService.changePassword(user,passwordModel.getNewPassword());
        return "password changed successFully!!";
    }

    @PostMapping(path = "/savePassword")
    public String savePassword(@RequestParam("token")String token,
                               @RequestBody PasswordModel passwordModel) {
        boolean isValidToken = userService.validatePasswordVerifyToken(token);
        if(!isValidToken)
        {
            return "Invalid token";
        }
        Optional<User> user=userService.getUserByToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "password changed!!";
        }else
        {
            return "invalid token : user not found";
        }

    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl + "savePassword?token=" + token;
        log.info("Click Link to reset password {}", url);
        return url;
    }

    private String applicationUrl(HttpServletRequest httpServletRequest) {
        return "http://" +
                httpServletRequest.getServerName() +
                ":" +
                httpServletRequest.getServerPort() + "/" +
                httpServletRequest.getContextPath();
    }
}
