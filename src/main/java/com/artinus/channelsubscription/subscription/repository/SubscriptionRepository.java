package com.artinus.channelsubscription.subscription.repository;

import com.artinus.channelsubscription.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionCustomRepository {
}
