package com.artinus.channelsubscription.subscription.domain;

public record RegisteredAccount(Long id, String phoneNumber, SubscriptionStatus currentSubscriptionStatus) {

}
