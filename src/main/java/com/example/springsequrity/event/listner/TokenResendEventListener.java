package com.example.springsequrity.event.listner;

import com.example.springsequrity.enitity.User;
import com.example.springsequrity.event.RegistrationCompleteEvent;
import com.example.springsequrity.event.TokenResendCompleteEvent;
import com.example.springsequrity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class TokenResendEventListener implements ApplicationListener<TokenResendCompleteEvent> {
    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(TokenResendCompleteEvent event) {
        //send email
        String url= event.getApplicationUrl()+"verifyRegistration?token="+event.getVerificationToken().getToken();
        log.info("Click the link to verify your account: {}",
                url);
    }
}
