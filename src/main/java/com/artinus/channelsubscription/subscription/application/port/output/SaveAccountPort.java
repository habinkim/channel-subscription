package com.artinus.channelsubscription.subscription.application.port.output;

import com.artinus.channelsubscription.subscription.domain.RegisteredAccount;
import com.artinus.channelsubscription.subscription.domain.SubscriptionStatus;

public interface SaveAccountPort {
    RegisteredAccount createAccount(final String phoneNumber);

    RegisteredAccount updateCurrentSubscriptionStatus(final String phoneNumber, final SubscriptionStatus status);
}
