package com.example.springsequrity.event.listner;

import com.example.springsequrity.enitity.User;
import com.example.springsequrity.event.RegistrationCompleteEvent;
import com.example.springsequrity.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user=event.getUser();
        String token= UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user,token);

        //send email
        String url= event.getApplicationUrl()+"verifyRegistration?token="+token;
        log.info("Click the link to verify your account: {}",
                url);
    }
}
