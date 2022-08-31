package com.example.springsequrity.event;

import com.example.springsequrity.enitity.User;
import com.example.springsequrity.enitity.VerificationToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TokenResendCompleteEvent extends ApplicationEvent {

    private String applicationUrl;
    private VerificationToken verificationToken;
    public TokenResendCompleteEvent(VerificationToken verificationToken, String applicationUrl) {
        super(verificationToken);
        this.applicationUrl=applicationUrl;
        this.verificationToken=verificationToken;
    }
}
