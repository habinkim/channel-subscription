package com.artinus.channelsubscription.subscription.application.port.output;

import com.artinus.channelsubscription.subscription.domain.RegisteredAccount;

import java.util.Optional;

public interface LoadAccountPort {

    Optional<RegisteredAccount> findByPhoneNumber(String phoneNumber);

}
